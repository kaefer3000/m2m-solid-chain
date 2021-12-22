# Introduction

This program contains two agents (a seller and a customer) to facilitate an automatic purchase process based on linked data. The agents communicate by sending messages via HTTP requests to the other agent's inbox. Messages are fully expressed in RDF and stored on SoLiD Pods, which allow the users to have full control over their data. The agents are written in the Notation3 language with [ASM4LD](http://ceur-ws.org/Vol-2073/article-05.pdf) semantics, see also [Linked Data-Fu](http://linked-data-fu.github.io/).

# Setup Solid Pods
In order for the agents to work, seller and customer need SoLiD Pods. This can be done on your own or by using one of serveral providers. See https://solidproject.org/users/get-a-pod.
The code currently uses https://seller0.solidweb.org/profile/card#me and https://customer1.solidweb.org/profile/card#me for seller and customers respectively.
We do not guarantee the availability of those publicly hosted Solid Pods.

For the seller, add an Linked Data Platform Container: inventory.
To create goods that should be sold by be seller, add a Turtle file (.ttl) with the form to the inventory:

	@prefix schema: <http://schema.org/> .
	@prefix : <#>.

	:obj 
	  a schema:Product ;
	  schema:category "screw" ; 						 			  #category in String
	  schema:name "a21" . 								 			  #name in String

	<https://seller0.solidweb.org/profile/card#me> schema:owns :obj . #seller's SoLiD WebID
Change the URI in the lines 14 and 20 of the `agents/SAgent.n3` file to your seller's inbox URI.

For the customer, add a monetary limit to the customer's WebID profile card. The exemplary triples would look like this:

	:me
		schema:floorLimit :limit.

	:limit 
		a schema:MonetaryAmount; 
		schema:currency "ETH";
		schema:value 30 .			#limit in integer
 
Change the URI in the lines 18 and 23 of the `agents/CAgent.n3` file to your customer's inbox URI. 

# Scripts

The script should work on Linux, Mac, and Windows. We did not have a Mac to test.

## Inititalise

Run `init.sh` (Linux/Mac) or `init.bat` (Windows) to get the agent runtime stored in a temporary directory.

## Run the Customer Agent
Perform the steps above. Your agents needs to have full rights to delete, retrieve and update information in your inbox folder. Change the access rights in the Solid Pod accordingly.
If done start the agent through the command prompt:

`run-customer.sh` (Linux/Mac) or `run-customer.bat` (Windows).

## Run the Seller Agent
Perform the steps above. Your agents needs to have full rights to delete, retrieve and update information in your inbox folder. Change the access rights in the Solid Pod accordingly.
If done start the agent through the command prompt:

`run-seller.sh` (Linux/Mac) or `run-seller.bat` (Windows).

