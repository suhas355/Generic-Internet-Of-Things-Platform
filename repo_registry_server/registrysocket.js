var exports = module.exports = {};
var PORT = 33333;
var HOST = '127.0.0.1';
var db = require("./dbconnection.js");

var dgram = require('dgram');
var message = new Buffer('ping');
var properFilter = false;
var client = dgram.createSocket('udp4'); 

client.on('listening', function () { 
	var address = client.address(); 
	console.log('UDP Server listening on ' + address.address + ":" + address.port); 
}); 
	
client.on('message', function (message, remote) {

	//console.log(remote.address + ':' + remote.port +' - ' + message); 
	if( message == "ok"){
		properFilter=true;
	}

	//client.close(); 
}); 

exports.pingFilterServer = function(){
	if(properFilter==false){
		console.log("filter server dead");
		db.insertFSPingStatus("filter","inactive");

	}else{
		console.log("filter server ok");
		db.insertFSPingStatus("filter","active");
	}
	properFilter = false;
	client.send(message, 0, message.length, PORT, HOST, function(err, bytes) { 
	if (err) 
		throw err; 
	console.log('Test ping ' + HOST +':'+ PORT); 
	});
}


exports.pingGateway = function(){
	console.log("bla");
}

//setInterval(bla,1000);
