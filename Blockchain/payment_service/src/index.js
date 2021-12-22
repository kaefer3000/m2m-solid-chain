//
import { XMLHttpRequest } from "xmlhttprequest";
import { sendEther, parseWeiToEther, parsePriceToWei } from "../lib/blockchain.js";
import { createLinkedDataNotification, postResource, updateFailedMoneyTransfer } from "../lib/solid.js";
import express from "express";
import logger from "morgan";
import bodyParser from "body-parser";
import rdf from "rdf-ext";
import rdfBodyParser from "rdf-body-parser";
import Web3 from "web3";

// The root app
var app = express();

var configuredBodyParser = rdfBodyParser({'defaultMediaType' : 'text/turtle' });

app.use(configuredBodyParser);

// configuring the app
app.set('json spaces', 2);
app.set('case sensitive routing', true);
app.set('strict routing', true);
app.use(logger('dev'));

// defining a utility method that redirects (301) missing trailing slashes
var redirectMissingTrailingSlash = function(request, response, next) {
  if (!request.originalUrl.endsWith('/'))
    response.redirect(301, request.originalUrl + '/');
  else
    next();
};

// Getting balance of the account with the address given in the URI
app.route("/balance/account/:id").get(function(request, response) {
	
	  var account = request.params.id;
	  var integer = new rdf.NamedNode('http://www.w3.org/2001/XMLSchema#integer');
	  
	  const balance = getBalance(account);
	  (async () => {
		 var balanceGraph = rdf.createGraph();
			balanceGraph.addAll(
			  [
				new rdf.Triple(
				  new rdf.NamedNode('#account'),
				  new rdf.NamedNode('http://www.w3.org/1999/02/22-rdf-syntax-ns#type'),
				  new rdf.NamedNode('http://ethon.consensys.net/ExternalAccount')),
				new rdf.Triple(
				  new rdf.NamedNode('#account'),
				  new rdf.NamedNode('http://ethon.consensys.net/address'),
				  new rdf.Literal(account)),
				new rdf.Triple(
				  new rdf.NamedNode('#account'),
				  new rdf.NamedNode('http://ethon.consensys.net/accountBalance'),
				  new rdf.Literal(await balance, null, integer, null))
			  ]) 
		 response.sendGraph(balanceGraph);	  
	  })()
});

		async function getBalance (account) {
			var balance;
			try {
			  const provider = new Web3.providers.HttpProvider("http://ganache:8545");
			  const web3 = new Web3(provider);
			  
			  balance = await web3.eth.getBalance(account);
			} catch (error) {
			  console.log(error);
			}
			return await balance;
		  };

// POSTing Linked Data Notification
app.route("/").post(function(request, response) {

	  var senderTripleCount = 0;
	  var receiverTripleCount = 0;
	  var customerTripleCount = 0;
	  var priceTripleCount = 0;
	  var moneyTransferTripleCount = 0;
	  var objectTripleCount = 0;
	  
	  var objectTriple;
      var senderTriple;
	  var receiverTriple;
	  var customerTriple;
	  var priceTriple;
	  var moneyTransferTriple;
	  
		request.graph.filter(
			function(triple) {
			  return triple.predicate.nominalValue === 'http://ethon.consensys.net/from'
			}).forEach(function(triple) {
			  ++senderTripleCount;
			  senderTriple = triple;
			})
		request.graph.filter(
			function(triple) {
			  return triple.predicate.nominalValue === 'http://ethon.consensys.net/to'
			}).forEach(function(triple) {
			  ++receiverTripleCount;
			  receiverTriple = triple;
			})
		request.graph.filter(
			function(triple) {
			  return triple.predicate.nominalValue === 'http://schema.org/agent'
			}).forEach(function(triple) {
			  ++customerTripleCount;				
			  customerTriple = triple;
			})
		request.graph.filter(	
			function(triple) {
			  return triple.predicate.nominalValue === 'http://schema.org/amount'
			}).forEach(function(triple) {
			  ++priceTripleCount;			  
			  priceTriple = triple;
			})
		request.graph.filter(	
			function(triple) {
			  return triple.predicate.nominalValue === 'http://schema.org/object'
			}).forEach(function(triple) {
			  ++objectTripleCount;			  
			  objectTriple = triple;
			})
		request.graph.filter(	
			function(triple) {
			  return triple.predicate.nominalValue === 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'
			}).forEach(function(triple) {
			  ++moneyTransferTripleCount;			  
			  moneyTransferTriple = triple;
			})
		
      if (senderTripleCount === 0) {
          response.status(400);
          response.send('Please supply a triple with ethon:from as predicate');
          return;
      }
	  if (receiverTripleCount === 0) {
          response.status(400);
          response.send('Please supply a triple with ethon:to as predicate');
          return;
      }
	  if (customerTripleCount === 0) {
          response.status(400);
          response.send('Please supply a triple with schema:agent as predicate');
          return;
      }
	  if (priceTripleCount === 0) {
          response.status(400);
          response.send('Please supply a triple with schema:amount as predicate');
          return;
      }
	  if (moneyTransferTripleCount === 0) {
          response.status(400);
          response.send('Please supply a triple with rdf:type as predicate');
          return;
      }
	   if (objectTripleCount === 0) {
          response.status(400);
          response.send('Please supply a triple with schema:object as predicate');
          return;
      }
	  
      var sender;
	  var receiver;
	  var customer;
	  var price;
	  var moneyTransfer;
	  var object;

	  sender = senderTriple.object.nominalValue;
	  receiver = receiverTriple.object.nominalValue;
	  customer = customerTriple.object.nominalValue;
	  price = priceTriple.object.nominalValue;
	  object = objectTriple.object.nominalValue;
	  moneyTransfer = moneyTransferTriple.subject.nominalValue;
	  
	  const statusCode = handlePay(moneyTransfer, price, sender, receiver, customer, object);
	  (async () => {
		response.sendStatus(await statusCode)
	  })()
	  return;
});

		async function handlePay (moneyTransfer, price, sender, receiver, customer, object) {
			
			var statusCode;
			
			try {
				
			  const txData = `SOLIDBLOCKCHAIN_TX_DATA,${moneyTransfer},${customer},${price}`;
			  
			  const provider = new Web3.providers.HttpProvider("http://ganache:8545");
			  const web3 = new Web3(provider);
			  
			  const senderBalance = await web3.eth.getBalance(sender);
			  const priceInWei = await parsePriceToWei(price);
			  if (parseInt(priceInWei) < parseInt(senderBalance)) {
				  
				  // make payment for the resource
				  const transactionHash = await sendEther(
					web3,
					sender,
					receiver,
					price,
					txData
				  );

				  // create Linked Data Notification
				  const notification = createLinkedDataNotification(
					sender,
					receiver,
					customer,
					moneyTransfer,
					price,
					transactionHash
				  );

				  var xhr = new XMLHttpRequest();
					xhr.open("POST", 'https://seller0.solidweb.org/inbox/', true);
					xhr.setRequestHeader("Content-Type", "text/turtle");
					xhr.send(notification);
					
				  const senderBalance = await web3.eth.getBalance(sender);
				  const receiverBalance = await web3.eth.getBalance(receiver);
				  console.log("Payment completed!");	
				  console.log("New balance: ");
				  console.log("Sender (" + sender + "): " + parseWeiToEther(senderBalance) + " ETH");
				  console.log("Receiver (" + receiver + "): " + parseWeiToEther(receiverBalance) + " ETH");
				  statusCode = 201;
			  } else {
				  
				  const resourceURL = moneyTransfer.split('#')[0];

				  const update = updateFailedMoneyTransfer (
				    sender,
				    receiver,
				    customer,
				    moneyTransfer,
				    price,
				    object
				  )
				  
				  var xhr = new XMLHttpRequest();
					xhr.open("PUT", resourceURL, true);
					xhr.setRequestHeader("Content-Type", "text/turtle");
					xhr.send(update);
					
				  console.log("Account balance not sufficient to cover transaction!");
				  statusCode = 201;
			  }
			} catch (error) {
			  console.log(error);
			  statusCode = 400;
			}
			return await statusCode;
		  };

// Startup the server
var port = 4000;
app.listen(port, function () {
  console.log('Payment app listening on port ' + port);
});


