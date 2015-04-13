var exports = module.exports = {};
var PORT = 33333;
var HOST = 'localhost';//'192.168.217.104';
var mongoose = require('mongoose');
var db = require("./dbconnection");

var dbName = 'sensor';
var connectionString = 'mongodb://localhost:27017/' + dbName;
 
mongoose.connect(connectionString);

var dgram = require('dgram');
var message = new Buffer('ping');
var properFilter = false;
var gatewayStatus = {};
var client = dgram.createSocket('udp4'); 

client.on('listening', function () { 
	var address = client.address(); 
	console.log('UDP Server listening on ' + address.address + ":" + address.port); 
}); 
	
client.on('message', function (message, remote) {

	console.log("Ping RESPONSE---" +remote.address + ':' + remote.port +' - ' + message); 

	if( message == "ok"){
		properFilter=true;
	} 
	else {

		var obj = JSON.parse(message.toString());
		console.log(obj);
		var gatewayId = obj["mac"];
		for(var key in obj) {
 		   value = obj[key];
 		   if(key!="gid" && key!="mac"){
	 		   if(value == "0"){
	 		   		db.insertFSPingStatus("S"+key, "inactive");
	 		   } else{
	 		   		db.insertFSPingStatus("S"+key, "active");
	 		   }
 			}

 			else if(key == "mac"){

 				db.insertFSPingStatus("G"+obj[key], "active");
 				gatewayStatus[obj[key]] = 'active';
 			}
		}
	}

}); 

exports.pingFilterServer = function(){
	if(properFilter==false){
		console.log("filter server is inactive");
		db.insertFSPingStatus("filter","inactive");

	}else{
		console.log("filter server ok");
		db.insertFSPingStatus("filter","active");
	}
	properFilter = false;
	client.send(message, 0, message.length, PORT, HOST, function(err, bytes) { 
	if (err) 
		throw err; 
	console.log('Ping filter server ' + HOST +':'+ PORT); 
	});
}


exports.pingGateway = function(){
	
	if(gatewayStatus[db.gatewayList[0]] == 'inactive'){
		db.insertFSPingStatus('G'+db.gatewayList[0], "inactive");
	}
		gatewayStatus[db.gatewayList[0]] = "inactive";
		client.send(message, 0, message.length, PORT, "192.168.217.104", function(err, bytes) { 
		console.log('Ping gateway ' + HOST +':'+ PORT); 
	});
}

var SERVER_PORT = 30001;
var SERVER_HOST = 'localhost';//'192.168.217.106';

var server_2 = dgram.createSocket('udp4');

server_2.on('listening',function(){
	var address = server_2.address();
	console.log('Registry Server Listening '+ address.address + ":" +address.port);
});

//Get sensor list is called to this place
server_2.on('message',function (message, remote){
	console.log(remote.address +":"+remote.port +" - " + message);
	var msg = db.getSensorList(message, function(data){
		var msg = new Buffer(JSON.stringify(data));
		server_2.send(msg,0,msg.length,remote.port,remote.address, function(err, bytes){
		if(err)
			throw err;
		});
	});
});

server_2.bind(SERVER_PORT,SERVER_HOST);
 

