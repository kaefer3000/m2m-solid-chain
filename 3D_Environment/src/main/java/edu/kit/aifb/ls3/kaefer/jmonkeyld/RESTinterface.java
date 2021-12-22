package edu.kit.aifb.ls3.kaefer.jmonkeyld;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.semanticweb.yars.jaxrs.trailingslash.NotFoundOnTrailingSlash;
import org.semanticweb.yars.jaxrs.trailingslash.RedirectMissingTrailingSlash;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.FOAF;
import org.semanticweb.yars.nx.namespace.RDF;
import org.semanticweb.yars.nx.namespace.RDFS;
import org.semanticweb.yars.nx.namespace.XSD;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

@Path("/")
public class RESTinterface {

	static Resource geovocFeature = new Resource("<http://geovocab.org/spatial#Feature>", true);

	static final class SCENE {
		private static final String namespace_scene = "http://vocab.arvida.de/2014/03/scenegraph/vocab#";

		static final Resource SceneNode = new Resource("<" + namespace_scene + "SceneNode" + ">", true);
		static final Resource nodeFixedCoordinateSystem = new Resource(
				"<" + namespace_scene + "nodeFixedCoordinateSystem" + ">", true);
		static final Resource DefinedSpatialRelationship = new Resource(
				"<" + namespace_scene + "DefinedSpatialRelationship" + ">", true);
	}

	static final class MATHS {
		private static final String namespace_maths = "http://vocab.arvida.de/2014/03/maths/vocab#";

		static final Resource CartesianCoordinateSystem = new Resource(
				"<" + namespace_maths + "CartesianCoordinateSystem" + ">", true);
		static final Resource Vector3D = new Resource("<" + namespace_maths + "Vector3D" + ">", true);
		static final Resource RhCartesianCoordinateSystem3D = new Resource(
				"<" + namespace_maths + "RightHandedCartesianCoordinateSystem3D" + ">", true); // was lowercase
		static final Resource x = new Resource("<" + namespace_maths + "x" + ">", true);
		static final Resource y = new Resource("<" + namespace_maths + "y" + ">", true);
		static final Resource z = new Resource("<" + namespace_maths + "z" + ">", true);
	}

	static final class SPATIAL {
		private static final String namespace_spatial = "http://vocab.arvida.de/2014/03/spatial/vocab#";

		static final Resource translation = new Resource("<" + namespace_spatial + "translation" + ">", true);
		static final Resource Translation3D = new Resource("<" + namespace_spatial + "Translation3D" + ">", true);
		static final Resource sourceCoordinateSystem = new Resource(
				"<" + namespace_spatial + "sourceCoordinateSystem" + ">", true);
		static final Resource targetCoordinateSystem = new Resource(
				"<" + namespace_spatial + "targetCoordinateSystem" + ">", true);
	}

	static final class VOM {
		private static final String namespace_vom = "http://vocab.arvida.de/2014/03/vom/vocab#";

		static final Resource quantityValue = new Resource("<" + namespace_vom + "quantityValue" + ">", true);
	}
	
	static final class PTO {
		private static final String namespace_pto = "http://www.productontology.org/id/";

		static final Resource ROBOTIC_ARM = new Resource("<" + namespace_pto + "Robotic_arm" + ">", true);
		static final Resource CONVEYOR_BELT = new Resource("<" + namespace_pto + "Conveyor_belt" + ">", true);
	}

	static final class UKEAQ {
		private static final String voc = "http://www.student.kit.edu/~ukeaq/uni/voc.ttl#";

		static final Resource hasStatus = new Resource("<" + voc + "hasStatus" + ">", true);
		static final Resource hasAction = new Resource("<" + voc + "hasAction" + ">", true);
		static final Resource hasCurrentAction = new Resource("<" + voc + "hasCurrentAction" + ">", true);
		static final Resource hasCurrentState = new Resource("<" + voc + "hasCurrentState" + ">", true);
		static final Resource grip = new Resource("<" + voc + "grip" + ">", true);
		static final Resource up_left = new Resource("<" + voc + "up_left" + ">", true);
		static final Resource down_right = new Resource("<" + voc + "down_right" + ">", true);
		static final Resource drop = new Resource("<" + voc + "drop" + ">", true);
		static final Resource drop_left = new Resource("<" + voc + "drop_left" + ">", true);
		static final Resource down_left = new Resource("<" + voc + "down_left" + ">", true);
		static final Resource up_right = new Resource("<" + voc + "up_right" + ">", true);
		static final Resource open = new Resource("<" + voc + "open" + ">", true);
		static final Resource closed = new Resource("<" + voc + "closed" + ">", true);
		static final Resource full = new Resource("<" + voc + "full" + ">", true);
		static final Resource empty = new Resource("<" + voc + "empty" + ">", true);
		static final Resource hasGripperState = new Resource("<" + voc + "hasGripperState" + ">", true);
		static final Resource hasCapacity = new Resource("<" + voc + "hasCapacity" + ">", true);
		static final Resource invokeAction = new Resource("<" + voc + "invokeAction" + ">", true);
		static final Resource on = new Resource("<" + voc + "on" + ">", true);
		static final Resource off = new Resource("<" + voc + "off" + ">", true);
		static final Resource on_back = new Resource("<" + voc + "on_back" + ">", true);
		static final Resource hasSensorInState = new Resource("<" + voc + "hasSensorInState" + ">", true);
		static final Resource hasSensorOutState = new Resource("<" + voc + "hasSensorOutState" + ">", true);
		static final Resource machine = new Resource("<" + voc + "machine" + ">", true);
		static final Resource storage = new Resource("<" + voc + "storage" + ">", true);
		static final Resource fill = new Resource("<" + voc + "fill" + ">", true);
		static final Resource moving = new Resource("<" + voc + "moving" + ">", true);
		static final Resource done = new Resource("<" + voc + "done" + ">", true);

	}
	
	static final class SOSA {
		private static final String namespace_sosa = "http://www.w3.org/ns/sosa/";

		static final Resource hosts = new Resource("<" + namespace_sosa + "hosts" + ">", true);
		static final Resource ACTUABLEPROPERTY = new Resource("<" + namespace_sosa + "ActuatableProperty" + ">", true);
		static final Resource OBSERVABLEPROPERTY = new Resource("<" + namespace_sosa + "ObservableProperty" + ">", true);
	}

	static final class SSN {
		private static final String namespace_ssn = "http://www.w3.org/ns/ssn/";

		static final Resource SYSTEM = new Resource("<" + namespace_ssn + "System" + ">", true);
		static final Resource hasSubSystem = new Resource("<" + namespace_ssn + "hasSubSystem" + ">", true);
	}

	@Context
	ServletContext ctx;

	@GET
	@RedirectMissingTrailingSlash
	public Response overview(@Context UriInfo uInfo) {
		// TODO: Write
		Resource doc = new Resource("<" + uInfo.getAbsolutePath().toString() + ">", true);
		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#virtual-factory>", true);

		List<Node[]> l = new LinkedList<Node[]>();
		l.add(new Node[] { doc, FOAF.PRIMARYTOPIC, self });
		
		l.add(new Node[] {self, RDF.TYPE, SSN.SYSTEM });
		l.add(new Node[] {self, RDFS.LABEL, new Literal("\"Virtual Flamethrower Production Site\"@en", true) });

		// Create arms overview
		@SuppressWarnings("unchecked")
		Map<Integer, com.jme3.scene.Node> arms = (Hashtable<Integer, com.jme3.scene.Node>) ctx
				.getAttribute(ContextListenerApp.ARMS);
		if (arms == null)
			throw new InternalServerErrorException();
		for (Integer i : arms.keySet()) {
			Resource r = new Resource("<" + uInfo.getAbsolutePath().toString() + "arm/" + i + "#arm>", true);
			l.add(new Node[] { self, SSN.hasSubSystem, r });
		}

		// Create storage overview
		@SuppressWarnings("unchecked")
		Map<Integer, com.jme3.scene.Node> storages = (Hashtable<Integer, com.jme3.scene.Node>) ctx
				.getAttribute(ContextListenerApp.STORAGE);
		if (storages == null)
			throw new InternalServerErrorException();
		for (Integer i : storages.keySet()) {
			Resource r = new Resource("<" + uInfo.getAbsolutePath().toString() + "storage/" + i + "#storage>", true);
			l.add(new Node[] { self, SSN.hasSubSystem, r });
		}

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();
	}

	@GET
	@Path("worldCoordinateSystem")
	@NotFoundOnTrailingSlash
	public Response worldCSg(@Context UriInfo uInfo) {

		Resource doc = new Resource("<" + uInfo.getAbsolutePath().toString() + ">", true);
		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#worldCS>", true);

		Iterable<Node[]> l = Arrays.asList(new Node[] { doc, FOAF.PRIMARYTOPIC, self },
				new Node[] { self, RDF.TYPE, MATHS.CartesianCoordinateSystem }, new Node[] { self, RDFS.COMMENT,
						new Literal("\"Coordinate system of this virtual world. The origin.\"@en", true) });

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();
	}

	@GET
	@Path("arm/{id}")
	@NotFoundOnTrailingSlash
	public Response arm(@PathParam(value = "id") int id, @Context UriInfo uInfo) {

		@SuppressWarnings("unchecked")
		Map<Integer, com.jme3.scene.Node> arms = (Hashtable<Integer, com.jme3.scene.Node>) ctx
				.getAttribute(ContextListenerApp.ARMS);
		if (arms == null)
			throw new InternalServerErrorException();
		Spatial s = (Spatial) arms.get(id);
		if (s == null)
			throw new NotFoundException();

		Resource doc = new Resource("<" + uInfo.getAbsolutePath().toString() + ">", true);
		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#arm>", true);
		Resource selfLoc = new Resource("<" + uInfo.getAbsolutePath().toString() + "#loc>", true);
		Resource selfLocData = new Resource(
				"<" + uInfo.getAbsolutePath().resolve(id + "/location-data#currentLocation").toString() + ">", true);
		Resource selfCoordinateSystem = new Resource("<" + uInfo.getAbsolutePath().toString() + "#selfCS>", true);
		Resource self2worldSpatialRelation = new Resource("<" + uInfo.getAbsolutePath().toString() + "#selfSR>", true);
		Resource worldCoordinateSystem = new Resource(
				"<" + uInfo.getAbsolutePath().resolve("/worldCoordinateSystem#worldCS").toString() + ">", true);
		Resource action = new Resource("<" + uInfo.getAbsolutePath().resolve(id + "/action#currentAction").toString() + ">",
				true);
		Resource state = new Resource("<" + uInfo.getAbsolutePath().resolve(id + "/state#currentState").toString() + ">",
				true);

		Iterable<Node[]> l = Arrays.asList(new Node[][] { new Node[] { doc, FOAF.PRIMARYTOPIC, self },
				new Node[] { self, RDF.TYPE, SCENE.SceneNode },
				new Node[] { self, RDF.TYPE, PTO.ROBOTIC_ARM },
				new Node[] { self, RDFS.LABEL, new Literal("\"Arm " + id + "\"@en", true) },
				new Node[] { self, SCENE.nodeFixedCoordinateSystem, selfCoordinateSystem },
				new Node[] { selfCoordinateSystem, RDF.TYPE, MATHS.CartesianCoordinateSystem },
				new Node[] { self2worldSpatialRelation, RDF.TYPE, SCENE.DefinedSpatialRelationship },
				new Node[] { self2worldSpatialRelation, SPATIAL.sourceCoordinateSystem, worldCoordinateSystem },
				new Node[] { self2worldSpatialRelation, SPATIAL.targetCoordinateSystem, selfCoordinateSystem },
				new Node[] { self2worldSpatialRelation, SPATIAL.translation, selfLoc },
				new Node[] { selfLoc, RDF.TYPE, SPATIAL.Translation3D },
				new Node[] { selfLoc, VOM.quantityValue, selfLocData },
				new Node[] { self, UKEAQ.hasCurrentAction, action }, 
				new Node[] { self, UKEAQ.hasCurrentState, state }
		});

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();
	}

	/*
	 * ============================== ARM
	 */
	@GET
	@Path("arm/{id}/location-data")
	@NotFoundOnTrailingSlash
	public Response armlocation(@PathParam(value = "id") int id, @Context UriInfo uInfo) {

		@SuppressWarnings("unchecked")
		Map<Integer, com.jme3.scene.Node> arms = (Hashtable<Integer, com.jme3.scene.Node>) ctx
				.getAttribute(ContextListenerApp.ARMS);
		if (arms == null)
			throw new InternalServerErrorException();
		Spatial s = (Spatial) arms.get(id);
		if (s == null)
			throw new NotFoundException();

		Vector3f vec = ((Spatial) arms.get(id)).getWorldTranslation();

		Resource doc = new Resource("<" + uInfo.getAbsolutePath().toString() + ">", true);
		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#data>", true);

		Iterable<Node[]> l = Arrays.asList(new Node[][] { new Node[] { doc, FOAF.PRIMARYTOPIC, self },
				new Node[] { self, RDF.TYPE, MATHS.Vector3D },
				new Node[] { self, MATHS.x, new Literal(Float.toString(vec.getX()), XSD.FLOAT) },
				new Node[] { self, MATHS.y, new Literal(Float.toString(vec.getY()), XSD.FLOAT) },
				new Node[] { self, MATHS.z, new Literal(Float.toString(vec.getZ()), XSD.FLOAT) } });

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();
	}

	@GET
	@Path("arm/{id}/action")
	@NotFoundOnTrailingSlash
	public Response armactionG(@PathParam(value = "id") int id, @Context UriInfo uInfo) {

		@SuppressWarnings("unchecked")
		Map<Integer, String> anims = (Hashtable<Integer, String>) ctx.getAttribute(ContextListenerApp.ARM_ANIMS);
		if (anims == null)
			throw new InternalServerErrorException();

		String action = anims.get(id);
		if (action == null)
			throw new NotFoundException();

		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#action>", true);
		Resource actionstatus = new Resource("<http://www.student.kit.edu/~ukeaq/uni/voc.ttl#" + action + ">", true);

		Iterable<Node[]> l = Arrays.asList(new Node[][] { new Node[] { self, UKEAQ.hasStatus, actionstatus }, });

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();

	}

	@POST
	@Path("arm/{id}/action")
	@NotFoundOnTrailingSlash
	public Response armactionP(Iterable<Node[]> nxp, @PathParam(value = "id") int id, @Context UriInfo uInfo) {

		@SuppressWarnings("unchecked")
		Map<Integer, AnimChannel> channel = (Hashtable<Integer, AnimChannel>) ctx
				.getAttribute(ContextListenerApp.ARM_CHANNELS);
		@SuppressWarnings("unchecked")
		Map<Integer, String> anims = (Hashtable<Integer, String>) ctx.getAttribute(ContextListenerApp.ARM_ANIMS);
		@SuppressWarnings("unchecked")
		Map<Integer, String> armup = (ConcurrentHashMap<Integer, String>) ctx.getAttribute(ContextListenerApp.ARMUP);
		
		if (channel == null || anims == null || armup == null)
			throw new InternalServerErrorException();

		if (!channel.containsKey(id)) {
			throw new NotFoundException();
		}

		for (Node[] nx : nxp) {
			if (nx[1].equals(UKEAQ.invokeAction)) {
				//The grip action of a robot arm is not an animation but rather a reassignment of the gripped object from a previous node to the node of the robot arm.
				//The reassignment is handled in the simpleUdpate of the ContextListenerApp, since rootNode is often the previous node and a threading error occurs, if not handled in the simpleUpdate.
				if (nx[2].equals(UKEAQ.grip)) {
					//set the action indicator to moving, so that no other action is posted
					anims.put(id, "moving");
					//put the grip action into the arm update map, which is then handled in the simpleUpdate, since it needs to access rootNode
					armup.put(id, "grip");

				//The drop action of a robot arm is not an animation but rather a reassignment of the to be dropped object from the robot arm node to a succeeding node.
				} else if (nx[2].equals(UKEAQ.drop)) {
					anims.put(id, "moving");
					armup.put(id, "drop");
				}
				else if (nx[2].equals(UKEAQ.drop_left)) {
					anims.put(id, "moving");
					armup.put(id, "drop_left");
				}

				//Here the animation of the 3D robot arm, which we want to trigger, to move down right has as name "down" but is named as "down_right" in the vocabulary for clarity
				else if (nx[2].equals(UKEAQ.down_right)) {
					channel.get(id).setAnim("down");
					channel.get(id).setLoopMode(LoopMode.DontLoop);
				//Here the animation of the 3D robot arm, which we want to trigger, to move up right has as name "up" but is named as "up_right" in the vocabulary for clarity
				} else if (nx[2].equals(UKEAQ.up_right)) {
					channel.get(id).setAnim("up");
					channel.get(id).setLoopMode(LoopMode.DontLoop);
				//All other animation names correspond to the vocabulary names
				} else if (nx[2].equals(UKEAQ.down_left)) {
					channel.get(id).setAnim("down_left");
					channel.get(id).setLoopMode(LoopMode.DontLoop);
				} else {
					String s = nx[2].getLabel();
					String s1 = s.substring(s.indexOf("#") + 1);
					channel.get(id).setAnim(s1);
					channel.get(id).setLoopMode(LoopMode.DontLoop);
				}

			}

		}

		return Response.ok().build();

	}

	@GET
	@Path("arm/{id}/state")
	@NotFoundOnTrailingSlash
	public Response armstateG(@PathParam(value = "id") int id, @Context UriInfo uInfo) {

		@SuppressWarnings("unchecked")
		Map<Integer, Resource> states = (Hashtable<Integer, Resource>) ctx.getAttribute(ContextListenerApp.ARM_STATES);
		if (states == null)
			throw new InternalServerErrorException();
		if (!states.containsKey(id))
			throw new NotFoundException();

		@SuppressWarnings("unchecked")
		Map<Integer, SkeletonControl> skeletonControl = (Map<Integer, SkeletonControl>) ctx
				.getAttribute(ContextListenerApp.SKELCONTROL);
		com.jme3.scene.Node head = (com.jme3.scene.Node) skeletonControl.get(id).getAttachmentsNode("Bone.004");

		@SuppressWarnings("unchecked")
		Map<Integer, Map<Integer, com.jme3.scene.Node>> products = (Hashtable<Integer, Map<Integer, com.jme3.scene.Node>>) ctx
				.getAttribute(ContextListenerApp.PRODUCTS);
		@SuppressWarnings("unchecked")
		Map<Integer, Integer> storagec = (Hashtable<Integer, Integer>) ctx
				.getAttribute(ContextListenerApp.STORAGECOUNT);
		com.jme3.scene.Node rootNode = (com.jme3.scene.Node) ctx.getAttribute(ContextListenerApp.ROOTNODE);

		Resource gripperState = UKEAQ.open;
		Resource capacity = UKEAQ.empty; // TODO: Why is empty a capacity
		Resource sensor_in = UKEAQ.empty;
		Resource sensor_out = UKEAQ.empty;

		//Here the cases are for each single arm. arm/0 is handled in case 0, arm/1 is handled in case 1, etc.
		switch (id) {
		case 0:
			if (head.hasChild(products.get(0).get(0))) {
				gripperState = UKEAQ.closed;
				capacity = UKEAQ.full;
			} else {
				gripperState = UKEAQ.open;
				capacity = UKEAQ.empty;
			}
			if (rootNode.hasChild(products.get(0).get(0))) {
				sensor_in = UKEAQ.full;
			} else {
				sensor_in = UKEAQ.empty;
			}
			break;
		case 1:
			if (head.hasChild(products.get(0).get(0))) {
				gripperState = UKEAQ.closed;
				capacity = UKEAQ.full;
			} else {
				gripperState = UKEAQ.open;
				capacity = UKEAQ.empty;
			}

			if (storagec.get(1) == 0) {
				sensor_out = UKEAQ.empty;
			} else if (rootNode.hasChild(products.get(0).get(0)) && rootNode.hasChild(products.get(0).get(0))) {
				sensor_out = UKEAQ.full;
			} else if (storagec.get(1) == 1 && rootNode.hasChild(products.get(0).get(0))) {
				storagec.put(1, 2);
				sensor_out = UKEAQ.empty;
			} else if (storagec.get(1) == 2 && rootNode.hasChild(products.get(0).get(0))) {
				storagec.put(1, 1);
				sensor_out = UKEAQ.empty;
			} else {
				sensor_out = UKEAQ.empty;
			}
			if (rootNode.hasChild(products.get(0).get(0))) {
				sensor_in = UKEAQ.full;
			} else {
				sensor_in = UKEAQ.empty;
			}
			break;

		}

		Resource state = states.get(id);

		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#state>", true);

		Iterable<Node[]> l = Arrays.asList(new Node[][] { new Node[] { self, UKEAQ.hasStatus, state},
				new Node[] { self, UKEAQ.hasGripperState, gripperState },
				new Node[] { self, UKEAQ.hasCapacity, capacity },
				new Node[] { self, UKEAQ.hasSensorInState, sensor_in },
				new Node[] { self, UKEAQ.hasSensorOutState, sensor_out }

		});

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();

	}

	/*
	 * ================================= storage
	 */

	@GET
	@Path("storage/{id}")
	@NotFoundOnTrailingSlash
	public Response storage(@PathParam(value = "id") int id, @Context UriInfo uInfo) {

		Resource doc = new Resource("<" + uInfo.getAbsolutePath().toString() + ">", true);
		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#storage>", true);
		Resource selfLoc = new Resource("<" + uInfo.getAbsolutePath().toString() + "#loc>", true);
		Resource selfLocData = new Resource(
				"<" + uInfo.getAbsolutePath().resolve(id + "/location-data#currentLocation").toString() + ">", true);
		Resource selfCoordinateSystem = new Resource("<" + uInfo.getAbsolutePath().toString() + "#selfCS>", true);
		Resource self2worldSpatialRelation = new Resource("<" + uInfo.getAbsolutePath().toString() + "#selfSR>", true);
		Resource worldCoordinateSystem = new Resource(
				"<" + uInfo.getAbsolutePath().resolve("/worldCoordinateSystem#worldCS").toString() + ">", true);
		Resource action = new Resource("<" + uInfo.getAbsolutePath().resolve(id + "/action#currentAction").toString() + ">",
				true);
		Resource state = new Resource("<" + uInfo.getAbsolutePath().resolve(id + "/state#currentState").toString() + ">",
				true);

		Iterable<Node[]> l = Arrays.asList(new Node[][] { new Node[] { doc, FOAF.PRIMARYTOPIC, self },
				new Node[] { self, RDF.TYPE, UKEAQ.storage },
				new Node[] { self, RDFS.LABEL, new Literal("\"Storage container " + id + "\"@en", true) },
				new Node[] { self, SCENE.nodeFixedCoordinateSystem, selfCoordinateSystem },
				new Node[] { selfCoordinateSystem, RDF.TYPE, MATHS.CartesianCoordinateSystem },
				new Node[] { self2worldSpatialRelation, RDF.TYPE, SCENE.DefinedSpatialRelationship },
				new Node[] { self2worldSpatialRelation, SPATIAL.sourceCoordinateSystem, worldCoordinateSystem },
				new Node[] { self2worldSpatialRelation, SPATIAL.targetCoordinateSystem, selfCoordinateSystem },
				new Node[] { self2worldSpatialRelation, SPATIAL.translation, selfLoc },
				new Node[] { selfLoc, RDF.TYPE, SPATIAL.Translation3D },
				new Node[] { selfLoc, VOM.quantityValue, selfLocData },
				new Node[] { self, UKEAQ.hasCurrentAction, action },
				new Node[] { self, UKEAQ.hasCurrentState, state } });

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();
	}

	@GET
	@Path("storage/{id}/location-data")
	@NotFoundOnTrailingSlash
	public Response storagelocation(@PathParam(value = "id") int id, @Context UriInfo uInfo) {

		@SuppressWarnings("unchecked")
		Map<Integer, com.jme3.scene.Node> storages = (Hashtable<Integer, com.jme3.scene.Node>) ctx
				.getAttribute(ContextListenerApp.STORAGE);
		if (storages == null)
			return Response.status(404).build();

		Vector3f vec = ((Spatial) storages.get(id)).getWorldTranslation();

		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#data>", true);

		Iterable<Node[]> l = Arrays.asList(new Node[][] { new Node[] { self, RDF.TYPE, MATHS.Vector3D },
				new Node[] { self, MATHS.x, new Literal(Float.toString(vec.getX()), XSD.FLOAT) },
				new Node[] { self, MATHS.y, new Literal(Float.toString(vec.getY()), XSD.FLOAT) },
				new Node[] { self, MATHS.z, new Literal(Float.toString(vec.getZ()), XSD.FLOAT) } });

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();
	}

	@GET
	@Path("storage/{id}/state")
	@NotFoundOnTrailingSlash
	public Response storagestateG(@PathParam(value = "id") int id, @Context UriInfo uInfo) {

		@SuppressWarnings("unchecked")
		Map<Integer, String> storageStates = (Hashtable<Integer, String>) ctx.getAttribute(ContextListenerApp.STORAGE_STATES);

		com.jme3.scene.Node rootNode = (com.jme3.scene.Node) ctx.getAttribute(ContextListenerApp.ROOTNODE);

		@SuppressWarnings("unchecked")
		Map<Integer, Map<Integer, com.jme3.scene.Node>> products = (Hashtable<Integer, Map<Integer, com.jme3.scene.Node>>) ctx.getAttribute(ContextListenerApp.PRODUCTS);
		
		if (storageStates == null)
			return Response.status(404).build();
		
		com.jme3.scene.Node product0 = products.get(id).get(0);
		com.jme3.scene.Node product2 = products.get(id).get(2);

		String capacity = "";
		if (rootNode.hasChild(product2)) {
			capacity = "full";
		}
		else if (rootNode.hasChild(product0)){
			capacity = "non-empty";
		}
		else capacity = "empty";

		String state = storageStates.get(id);

		Resource self = new Resource("<" + uInfo.getAbsolutePath().toString() + "#state>", true);
		Resource fixstate = new Resource("<http://www.student.kit.edu/~ukeaq/uni/voc.ttl#" + state + ">", true);
		Resource fixcapacity = new Resource("<http://www.student.kit.edu/~ukeaq/uni/voc.ttl#" + capacity + ">", true);

		Iterable<Node[]> l = Arrays.asList(new Node[][] { new Node[] { self, UKEAQ.hasStatus, fixstate },
			new Node[] { self, UKEAQ.hasCapacity, fixcapacity }});

		return Response.ok(new GenericEntity<Iterable<Node[]>>(l) {
		}).build();

	}
}
