var PORT = 33333;
var HOST = '127.0.0.1';

var dgram = require('dgram');
var server = dgram.createSocket('udp4');

var i=0;

server.on('listening',function(){
	var address = server.address();
	console.log('Server Listening '+ address.address + ":" +address.port);
});

server.on('message',function (message, remote){
	console.log(remote.address +":"+remote.port +" - " + message);
	/*if( i>0 ){
		var res = JSON.parse(message.toJSON());
		console.log(res);
	}
	i++;*/
	var ok = new Buffer("ok");
	server.send(ok,0,ok.length,remote.port,remote.address, function(err, bytes){
		if(err)
			throw err;
		console.log('I am active ' + remote.address + ":"+remote.port);
	});
});

server.bind(PORT,HOST);