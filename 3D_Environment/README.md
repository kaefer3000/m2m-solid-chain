# Two Virtual Trading Robot Arms

To visually illustrate a Machine-to-Machine trading use case, this 3D Environment is used. The 3D Environment also adds another dimension to the use case, as the ownership will not only transferred but the possession of the object will also be handed over. 

There are two robot arms in the 3D Encironment. The left robot arm represents the seller while the right arm represents the customer. There are also three storage containers. The left container belongs to the seller and the right container belongs to the customer. The middle container represents a counter that belongs neither to the seller nor to the customer. These storage containers indicate the possession of the objects that will be traded. The objects that will be traded in our prototype are steel plates. In the starting position of our prototype, the steel plate is in the left container and therefore in the seller's possession. If the object is not in a container but gripped by one of the robot arms, then the object is in the possession of the robot arm that grips the object.

# Build
* You need Java 8, otherwise some of the VR dependencies do not work
* To build, you also need Maven, then you can run:

````
mvn package
````

## Run
Deploy the `.war` file on a Servlet engine such as Apache Tomcat or Eclipse Jetty, or simply use:

````
mvn jetty:run
````
