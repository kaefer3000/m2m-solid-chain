# Machine-to-Machine Trading in Virtual Reality via Linked Data Agents and the Distributed Ledger

Combine SoLid, Linked Data (REST+RDF) interface to JMonkey3 and Ethereum blockchain to create a Machine-to-Machine Payment prototype for agents to exchange resources for payment autonomously.

## Setup

Some prerequisites:
* Java 8
* Maven
* Docker-compose

    ### _More Informations on how to deploy, start and run the different components can be found in the README.md inside their corresponding folders._

For the start of the whole system, note the following hints.
- Start the 3D Environment and the Payment first, before the Linked Data Agents.
- To start a Machine-to-Machine Payment process, send a HTTP POST request with a demand RDF file to the seller's inbox. This would be an exemplary demand Turtle file:

        @prefix schema: <http://schema.org/> .
        @prefix this:  <#> .

        this:demand 
            a schema:Demand ;
            schema:itemOffered  <https://seller0.solidweb.org/inventory/a21.ttl#obj> .  #references an object in the seller's inventory
        <https://seller0.solidweb.org/profile/card#me> schema:offers  this:demand .     #seller's WebID
        <https://customer1.solidweb.org/profile/card#me> schema:seeks  this:demand .    #customer's WebID
        


