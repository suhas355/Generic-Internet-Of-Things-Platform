var exports = module.exports = {};
var PORT = 33333;
var HOST = '127.0.0.1';
var mongoose = require('mongoose');
var db = require("./dbconnection");

var dbName = 'sensor';
var connectionString = 'mongodb://localhost:27017/' + dbName;
 
mongoose.connect(connectionString);

var dgram = require('dgram');
var message = new Buffer('ping');
var properFilter = false;
var client = dgram.createSocket('udp4'); 

client.on('listening', function () { 
	var address = client.address(); 
	console.log('UDP Server listening on ' + address.address + ":" + address.port); 
}); 
	
client.on('message', function (message, remote) {

	console.log(remote.address + ':' + remote.port +' - ' + message); 
	if( message == "ok"){
		properFilter=true;
	}

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
	
	var gatewayList = db.gatewayList;
	var len = gatewayList.length;
	console.log("Sending ping message to gateway ");
	//Change IP address to gateways ip address
	client.send(message, 0, message.length, PORT, "127.0.0.1", function(err, bytes) { 
		/*if (err) 
			throw err; */
		console.log('Test ping ' + HOST +':'+ PORT); 
	});
}

var SERVER_PORT = 30001;
var SERVER_HOST = '127.0.0.1';

var server_2 = dgram.createSocket('udp4');

server_2.on('listening',function(){
	var address = server_2.address();
	console.log('Registry Server Listening '+ address.address + ":" +address.port);
});

server_2.on('message',function (message, remote){
	console.log(remote.address +":"+remote.port +" - " + message);
	var msg = db.getSensorList(message, function(message){
		//console.log(message);
		var msg = new Buffer(JSON.stringify(message));
		//console.log(msg.toString());
		server_2.send(msg,0,msg.length,remote.port,remote.address, function(err, bytes){
		if(err)
			throw err;
		console.log('I am active ' + remote.address + ":"+remote.port);
		});
	});
});

server_2.bind(SERVER_PORT,SERVER_HOST);
 

