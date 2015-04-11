var PORT = 33333;
var HOST = 'localhost';


var dgram = require('dgram');
var server = dgram.createSocket('udp4');

var i=0;

server.on('listening',function(){
	var address = server.address();
	console.log('Server Listening '+ address.address + ":" +address.port);
	//console.log(ifaces.address);
});

server.on('message',function (message, remote){
	console.log(remote.address +":"+remote.port +" - " + message);
	
	var ok = new Buffer("ok");
	server.send(ok,0,ok.length,remote.port,remote.address, function(err, bytes){
		if(err)
			throw err;
		console.log('I am active ' + remote.address + ":"+remote.port);
	});
});

server.bind(PORT,HOST);


/*****************************Client Part***********************************/

var CPORT = 33334;
var CHOST = '10.2.143.77';

var mongoose = require('mongoose');
var db = require("./dbconnect");

var dbName = 'sensor';
var connectionString = 'mongodb://localhost:27017/' + dbName;
 
mongoose.connect(connectionString);

var dgram = require('dgram');
var message = new Buffer('get');
var properFilter = false;
var client = dgram.createSocket('udp4'); 

client.on('listening', function () { 
	var address = client.address(); 
	console.log('UDP Server listening on ' + address.address + ":" + address.port); 
}); 
	
client.on('message', function (message, remote) {

	console.log("data reply from gateway"+remote.address + ':' + remote.port +' - ' + message); 
	//parse and dbstore

	db.insertsensordata(message);

}); 

exports.getgatewaydata = function(){

	client.send(message, 0, message.length, CPORT, CHOST, function(err, bytes) { 
	if (err) 
		throw err; 
	//console.log('Get data from gateway ' + CHOST +':'+ CPORT); 
	});
}



