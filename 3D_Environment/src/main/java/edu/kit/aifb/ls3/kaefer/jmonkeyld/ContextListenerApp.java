package edu.kit.aifb.ls3.kaefer.jmonkeyld;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.HttpMethod;

import org.semanticweb.yars.nx.Resource;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

import edu.kit.aifb.ls3.kaefer.jmonkeyld.RESTinterface.UKEAQ;

@WebListener
public class ContextListenerApp extends SimpleApplication implements ServletContextListener, AnimEventListener {

	static enum TransformationType {
		parent, model, world
	}

	static final String CAMERA = "camera";
	// added arm here
	static final String ARMS = "arm";
	static final String ARM_STATES = "states";
	static final String ARMUP = "armupdates";
	
	// added channel here
	static final String ARM_CHANNELS = "channel";
	static final String CONTROL = "control";
	static final String SKELCONTROL = "skeletonControl";
	static final String ARM_ANIMS = "anims";
	static final String STORAGE_STATES = "sstates";
	static final String SACTION = "saction";
	static final String STORAGE = "storage";
	static final String STORAGECOUNT = "storageCount";
	static final String STORAGEUP = "storageupdate";
	static final String ROOTNODE = "rootnode";
	static final String PRODUCTS = "products";
	
	static final String GEOMETRY = "geometry-";

	float scalingFactor = 10;

	static int quadDimension = 128;

	Map<Integer, Map<Integer, Node>> products = new Hashtable<Integer, Map<Integer, Node>>();
	
	Map<Integer, Node> arms = new Hashtable<Integer, Node>();
	Map<Integer, String> _armupdate = new ConcurrentHashMap<Integer, String>();
	Map<Integer, String> armAnims = new Hashtable<Integer, String>();
	Map<Integer, AnimChannel> armChannels = new Hashtable<Integer, AnimChannel>();
	Map<Integer, Resource> armStates = new Hashtable<Integer, Resource>();
	
	Map<Integer, String> _storageupdate = new ConcurrentHashMap<Integer, String>();
	Map<Integer, Integer> storageCount = new Hashtable<Integer, Integer>();
	Map<Integer, String> storageStates = new Hashtable<Integer, String>();
	Map<Integer, String> storageActions = new Hashtable<Integer, String>();

	Map<Integer, SkeletonControl> skelContr = new Hashtable<Integer, SkeletonControl>();

	// add ServletContext in order to talk with RESTInterface - NEEDS TO STAY!
	ServletContext _ctx;

	public void contextInitialized(ServletContextEvent sce) {

		_ctx = sce.getServletContext();

		// Register Servlet
		ServletRegistration sr = _ctx.addServlet("Two Trading Robot Arms JMonkey Linked Data REST Interface",
				org.glassfish.jersey.servlet.ServletContainer.class);
		sr.addMapping("/*");
		sr.setInitParameter(org.glassfish.jersey.server.ServerProperties.PROVIDER_PACKAGES,
				this.getClass().getPackage().getName() + ","
						+ org.semanticweb.yars.jaxrs.JerseyAutoDiscoverable.class.getPackage().getName());

		FilterRegistration fr;
		// Register and configure filter to handle CORS requests
		fr = _ctx.addFilter("cross-origin", org.eclipse.jetty.servlets.CrossOriginFilter.class.getName());
		fr.setInitParameter(org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_METHODS_PARAM,
				HttpMethod.GET + "," + HttpMethod.PUT + "," + HttpMethod.POST + "," + HttpMethod.DELETE);
		fr.addMappingForUrlPatterns(null, true, "/*");

		this.start();
	}

	public void contextDestroyed(ServletContextEvent arg0) {

		this.stop();
	}

	public void simpleInitApp() {
		/*
		 * Sky.
		 */
		Spatial skybox;
		Texture solidColor = assetManager.loadTexture("Textures/Sky/SolidCyan/square.png");
		skybox = SkyFactory.createSky(assetManager, solidColor, solidColor, solidColor, solidColor, solidColor,
				solidColor);
		rootNode.attachChild(skybox);
		_ctx.setAttribute(ROOTNODE, rootNode);

		Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat1.setColor("Color", ColorRGBA.Blue);

		/*
		 * Creating the Quad for the Map
		 */

		Quad q = new Quad(quadDimension, quadDimension);
		Geometry geom = new Geometry("surface", q);

		Set<Geometry> geometriesToBeMoved = new HashSet<Geometry>();
		geometriesToBeMoved.add(geom);

		/*
		 * Moving the Quad for the map around and the margin Quads.
		 */

		for (Geometry g : geometriesToBeMoved) {
			// Rotate the quad such that it lies on the floor
			Quaternion quat = new Quaternion();
			quat.fromAngleAxis(1.5f * FastMath.PI, new Vector3f(1, 0, 0));
			g.setLocalRotation(quat);

			// Get the center of the mesh (no matter the original pivot)
			Vector3f center = ((Geometry) g).getMesh().getBound().getCenter().clone();
			center = quat.multLocal(center);

			// Create the node to use as pivot
			Node newPivot = new Node();
			newPivot.setLocalTranslation(center);
			newPivot.attachChild(g);

			// Reverse the pivot to match the center of the mesh
			g.setLocalTranslation(center.negate());
		}

		/*
		 * Creating the content of the quad and loading the texture.
		 */

		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		geom.setMaterial(mat2.clone());

		rootNode.attachChild(geom);

		/*
		 * Done with the map Quad.
		 */

		// Camera:

		cam.setLocation(new Vector3f(1.3586091f, 7.263974f, 8.202747f));
		cam.setRotation(new Quaternion(-2.6203314E-4f, 0.9633732f, -0.26816243f, -9.406242E-4f));


		// cam.setLocation(new Vector3f(0, 16, 50));
		flyCam.setEnabled(true);
		flyCam.setMoveSpeed(20f);
		_ctx.setAttribute(CAMERA, cam);

		// HUD:
		setDisplayStatView(false);
		setDisplayFps(false);

		// Es werde Licht!
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
		rootNode.addLight(sun);

		// add arms and control here
		Quaternion yaw90 = new Quaternion();
		yaw90.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, -1, 0));

		Quaternion yaw180 = new Quaternion();
		yaw180.fromAngleAxis(FastMath.PI, new Vector3f(0, -1, 0));

		Quaternion yaw_90 = new Quaternion();
		yaw_90.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));

		int i;
		for (i = 0; i < 2; i = i + 1) {
			arms.put(i, (Node) assetManager.loadModel("Models/arm3.j3o"));
		}

		arms.get(0).setLocalTranslation(new Vector3f(0, 0.8f, 0));

		arms.get(1).setLocalTranslation(new Vector3f(3, 0.8f, 0));
		arms.get(1).setLocalRotation(yaw180);

		for (i = 0; i < 2; i = i + 1) {
			rootNode.attachChild(arms.get(i));
		}

		_ctx.setAttribute(ARMS, arms);

		// pedestals

		Map<Integer, Geometry> pedestals = new Hashtable<Integer, Geometry>();

		Box b1 = new Box(0.2f, 0.4f, 0.2f);
		Material matg = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		matg.setColor("Color", ColorRGBA.Gray);

		for (i = 0; i < 2; i = i + 1) {
			pedestals.put(i, new Geometry("Box", b1));
			pedestals.get(i).setMaterial(matg);
		}

		pedestals.get(0).setLocalTranslation(new Vector3f(0, 0.4f, 0));
		pedestals.get(1).setLocalTranslation(new Vector3f(3, 0.4f, 0));

		for (i = 0; i < 2; i = i + 1) {
			rootNode.attachChild(pedestals.get(i));
		}

		// storage
		Map<Integer, Node> storage = new Hashtable<Integer, Node>();

		for (i = 0; i < 3; i = i + 1) {
			storage.put(i, (Node) assetManager.loadModel("Models/storage.j3o"));
		}

		storage.get(0).setLocalTranslation(new Vector3f(-1.5f, 0, 0));
		storage.get(1).setLocalTranslation(new Vector3f(1.5f, 0, 0));
		storage.get(2).setLocalTranslation(new Vector3f(4.5f, 0, 0));

		for (i = 0; i < 3; i = i + 1) {
			rootNode.attachChild(storage.get(i));
		}

		_ctx.setAttribute(STORAGE, storage);

		for (i = 0; i < 3; i = i + 1) {
			storageCount.put(i, 0);
		}

		_ctx.setAttribute(STORAGECOUNT, storageCount);
		
		for (i = 0; i < 3; i = i + 1) {
			storageStates.put(i, "free");
		}
		_ctx.setAttribute(STORAGE_STATES, storageStates);
		

		for (i = 0; i < 3; i = i + 1) {
			storageActions.put(i, "done");
		}
		_ctx.setAttribute(SACTION, storageActions);

		// add products

		Map<Integer, Node> steel_plate = new Hashtable<Integer, Node>();
		for (i = 0; i < 2; i = i + 1) {
			steel_plate.put(i, (Node) assetManager.loadModel("Models/steel_plate.j3o"));
		}
		steel_plate.get(0).setLocalTranslation(new Vector3f(-1.5f, 0.75f, 0));
		rootNode.attachChild(steel_plate.get(0));
		storageCount.put(0, 1);

		products.put(0, steel_plate);

		_ctx.setAttribute(PRODUCTS, products);

		_ctx.setAttribute(ARMUP, _armupdate);
		_ctx.setAttribute(STORAGEUP, _storageupdate);

		// arm animation

		for (i = 0; i < 2; i = i + 1) {
			armAnims.put(i, "");
		}

		_ctx.setAttribute(ARM_ANIMS, armAnims);

		// arm states

		for (i = 0; i < 2; i = i + 1) {
			armStates.put(i, UKEAQ.down_left);
		}
		_ctx.setAttribute(ARM_STATES, armStates);

		// arm control
		Map<Integer, AnimControl> contr = new Hashtable<Integer, AnimControl>();

		for (i = 0; i < 2; i = i + 1) {
			contr.put(i, arms.get(i).getChild("Rmk3.010").getControl(AnimControl.class));
			contr.get(i).addListener(this);
			armChannels.put(i, contr.get(i).createChannel());
		}

		_ctx.setAttribute(CONTROL, contr);
		_ctx.setAttribute(ARM_CHANNELS, armChannels);

		// add skeletonControl

		for (i = 0; i < 2; i = i + 1) {
			skelContr.put(i, arms.get(i).getChild("Rmk3.010").getControl(SkeletonControl.class));
		}

		_ctx.setAttribute(SKELCONTROL, skelContr);

		// animations

		for (i = 0; i < 2; i = i + 1) {
			armChannels.get(i).setAnim("down_left");
			armChannels.get(i).setLoopMode(LoopMode.DontLoop);
		}

	}

	/**
	 * The update cycle of jMonkey's scene graph.
	 *
	 */
	public void simpleUpdate(float tpf) {
		com.jme3.scene.Node product = products.get(0).get(3);
		LocalDateTime start = null;
		LocalDateTime end = null;
		for (Integer id : _armupdate.keySet()) {
			//If the arm update map has grip saved attach the correct item to the robot arm
			if (_armupdate.get(id).equals("grip")) {
				com.jme3.scene.Node head = (com.jme3.scene.Node) skelContr.get(id).getAttachmentsNode("Bone.004");
				
				switch (id) {
				case 0:
					//put the state of the robot arm to busy
					storageStates.put(0, "busy");
					//the storage count of the previous storage box is subtracted by one, since the robot now grips the item
					storageCount.put(0, storageCount.get(0) - 1);
					//remove the visible item from the parent (rootNode) to not see it anymore
					products.get(0).get(storageCount.get(0)).removeFromParent();
					//save the item that is normaly attached to the robot arm in product, which is attached to the robot arm at the end of our switch case
					product = products.get(0).get(0);
					start = LocalDateTime.now();
					break;
				case 1:
					storageStates.put(1, "busy");
					storageCount.put(1, storageCount.get(1) - 1);
					products.get(0).get(storageCount.get(1)).removeFromParent();
					product = products.get(0).get(0);
					break;
				}
				//attach the item to the head of the corresponding robot arm to make it visible
				head.attachChild(product);
				product.setLocalTranslation(new Vector3f(0, 0.25f, 0));
				//remove the action from the map, so that it isn't done again
				_armupdate.remove(id);
				//set the states of the robot arms to free, since they are no longer doing an action
				switch (id) {
				case 0:
					storageStates.put(1, "free");
					break;
				case 1:
					storageStates.put(0, "free");
					break;
				}
				//set the action indicator to done, so that another action may be posted to the robot arm
				armAnims.put(id, "done");
			}

			else if (_armupdate.get(id).equals("drop")) {
				Integer numb;
				switch (id) {
				case 0:
					storageStates.put(1, "busy");
					storageCount.put(1, storageCount.get(1) + 1);
					products.get(0).get(storageCount.get(0)).removeFromParent();
					products.get(0).get(storageCount.get(0)).setLocalTranslation(new Vector3f(1.5f, 0.75f, 0));
					rootNode.attachChild(products.get(0).get(0));
					break;
				case 1:
					storageStates.put(2, "busy");
					storageCount.put(2, storageCount.get(2) + 1);
					products.get(0).get(storageCount.get(1)).removeFromParent();
					products.get(0).get(storageCount.get(1)).setLocalTranslation(new Vector3f(4.5f, 0.75f, 0));
					rootNode.attachChild(products.get(0).get(0));
					end = LocalDateTime.now();
					break;
				}
				_armupdate.remove(id);
				switch (id) {
				case 0:
					storageStates.put(0, "free");
					break;
				case 1:
					storageStates.put(1, "free");
					break;
				}
				armAnims.put(id, "done");

			}
			
			else if (_armupdate.get(id).equals("drop_left")) {
				Integer numb;
				switch (id) {
				case 0:
					storageStates.put(0, "busy");
					storageCount.put(0, storageCount.get(0) + 1);
					products.get(0).get(storageCount.get(0) - 1).removeFromParent();
					products.get(0).get(storageCount.get(0) - 1).setLocalTranslation(new Vector3f(-1.5f, 0.25f, 0));
					rootNode.attachChild(products.get(0).get(storageCount.get(0) - 1));
					break;
				}
				_armupdate.remove(id);
				switch (id) {
				case 0:
					storageStates.put(0, "free");
					break;
				}
				armAnims.put(id, "done");

			}
			
		}
		
		BufferedWriter writer;
		try {
			if (start != null) {
				writer = new BufferedWriter(new FileWriter("start.txt")); 
				writer.write(start.toString());				    
				writer.close();
			}
			if (end != null) {
				writer = new BufferedWriter(new FileWriter("end.txt")); 
				writer.write(end.toString());				    
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}

	@Override
	public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
		com.jme3.scene.Node product;
		com.jme3.scene.Node product1;

		for (int armChannel : armChannels.keySet()) {
			if (armChannels.get(armChannel).equals(channel)) {
				if (animName.equals("down")) {
					armStates.put(armChannel, UKEAQ.down_right);
				} else if (animName.equals("Rotate_180")) {
					armStates.put(armChannel, UKEAQ.up_right);
				} else if (animName.equals("Rotate180")) {
					armStates.put(armChannel, UKEAQ.up_left);
				} else if (animName.equals("up")) {
					armStates.put(armChannel, UKEAQ.up_right);
				} else if (animName.equals("down_left")) {
					armStates.put(armChannel, UKEAQ.down_left);
				} else {
					armStates.put(armChannel, new Resource("<" + "http://www.student.kit.edu/~ukeaq/uni/voc.ttl#" + animName + ">", true));
					_ctx.log("Careful, creating and not re-using a URI: " + armStates.get(armChannel));
				}
				armAnims.put(armChannel, "done");
			}
		}

	}

	@Override
	public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

		for (int armChannel : armChannels.keySet()) {
			if (armChannels.get(armChannel).equals(channel)) {
				armAnims.put(armChannel, "moving");
				armStates.put(armChannel, UKEAQ.moving);
			}
		}

	}
}
