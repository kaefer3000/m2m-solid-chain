import web3 from "web3";
import { parsePriceToWei } from "./blockchain.js";

/**
 * Returns a linked data notification for a transaction
 * @param {string} senderEthAddress ethereum address of the sender
 * @param {string} receiverEthAddress ethereum address of the receiver
 * @param {string} senderWebID webID of the sender
 * @param {string} resourceURL url of the resource
 * @param {string} price price of resource
 * @param {string} transactionHash hash from the transaction for resource access
 */
export const createLinkedDataNotification = (
  senderEthAddress,
  receiverEthAddress,
  senderWebID,
  resourceURL,
  price,
  transactionHash
) => {
  const msgPayload = resourceURL;
  const priceInWei = parsePriceToWei(price);

  return `
    @prefix : <#> .
    @prefix ethon: <http://ethon.consensys.net/> .
    @prefix solid: <http://www.w3.org/ns/solid/terms#> .
    @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
    
    :Transaction 
      a ethon:Tx;
      ethon:txHash "${transactionHash}"^^xsd:hexBinary;
      ethon:from :Sender;
      ethon:to :Receiver;
      ethon:value ${priceInWei};
      ethon:msgPayload <${msgPayload}>.

    :Sender 
      a ethon:Account, solid:Account;
      ethon:address "${senderEthAddress}"^^xsd:hexBinary;
      solid:account <${senderWebID}> .

    :Receiver 
      a ethon:Account;
      ethon:address "${receiverEthAddress}"^^xsd:hexBinary .
    `;
};

/**
 * Returns a updated money transfer with failed action status
 * @param {string} senderEthAddress ethereum address of the sender
 * @param {string} receiverEthAddress ethereum address of the receiver
 * @param {string} senderWebID webID of the sender
 * @param {string} resourceURL url of the resource
 * @param {string} price price of resource
 * @param {string} object invoice object of resource
 */
export const updateFailedMoneyTransfer = (
  senderEthAddress,
  receiverEthAddress,
  senderWebID,
  resourceURL,
  price,
  object
) => {
  return `
    @prefix ethon: <http://ethon.consensys.net/> .
	@prefix schema: <http://schema.org/> .
	@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

	 <${resourceURL}>
	  a schema:MoneyTransfer ;
	  ethon:from "${senderEthAddress}"^^xsd:hexBinary ;
	  schema:actionStatus schema:FailedActionStatus ;
	  schema:agent <${senderWebID}> ;
	  schema:amount ${price} ;
	  schema:currency "ETH";
	  schema:object <${object}> ;
	  ethon:to "${receiverEthAddress}"^^xsd:hexBinary .
    `;
};   

/**
 * Returns string in hex
 * @param {string} data
 */
function toHex(data) {
  return web3.utils.fromAscii(data);
}

/**
 * Post a Resource to a container (e.g. for LDNs)
 * @param {string} url url of container to put the resource
 * @param {function} fetch
 * @param {string|Blob} body information to send
 */
export const postResource = async (url, fetch, body) => {
  await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "text/turtle",
      Link: '<http://www.w3.org/ns/ldp#Resource>; rel="type"',
    },
    body: body,
  });
};

