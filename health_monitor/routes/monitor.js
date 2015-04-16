var exports = module.exports = {};

var lssoc = require('../monitor_socket');

var ip = 'localhost';//"192.168.217.106";

var express = require('express');
var router = express.Router();
var https = require('https');
var cbIds = [];
var requestify = require('requestify');

router.route('/').post(function(req,res){
	res.send('Good');
});

/**
JSON structure

{
	'type' : ['ecg','bp','highpulse']
}

*/

router.route('/getsensors').post(function(req, res){
	console.log("Recieved request for get sensors");
	requestify.post('http://'+ip+':3000/sensor/getsensors/type', req.body)
		.then(function(response) {
				console.log("Response from filter server " + response.getBody()); 
		    	cbIds.push(response.getBody()['id']);
		    	res.send(response.getBody());
		   
	},
	function(err){
		if(err){
			console.log('Error ' + err);
			res.status(err.getCode()).send(err.getBody());
		}
	});
});

/**
JSON structure

{
	'sensorId' : ['s1','s2','s3'],
	'frequency' : 10,
	'tillWhen' : 1429224576
}

*/

router.route('/register_frequency').post(function(req,res){
	console.log("Received request to register frequency " + req.body);
	requestify.post('http://'+ip+':5000/getdata/freqdata', req.body)
		.then(function(response) {
				console.log("Response from filter server " + response.getBody()); 
		    	cbIds.push(response.getBody()['id']);
		    	res.send(response.getBody());
		   
	},
	function(err){
		if(err){
			console.log('Error ' + err);
			res.status(err.getCode()).send(err.getBody());
		}
	});
});

/**
JSON structure

{
	'sensorId' : 's1',
	'minValue' : 60,
	'maxValue' : 100
}

*/

router.route('/register_callback').post(function(req,res){
	console.log("Received request to register callback " + req.body);
	requestify.post('http://'+ip+':5000/getdata/registercallback/sensors', req.body)
		.then(function(response) {
				console.log("Response from filter server " + response.getBody()); 
		    	cbIds.push(response.getBody()['id']);
		    	res.send(response.getBody());
		   
	},
	function(err){
		if(err){
			console.log('Error ' + err);
			res.status(err.getCode()).send(err.getBody());
		}
	});
});


/**
Parameter: GET
?id=c1

*/
router.route('/getresults').get(function(req, res){
	console.log('Get result for callback with id ' + req.query.id 
		+ ' from application ip ' + req.connection.remoteAddress);
	var respArr = lssoc.cb_resp;
	var flag = false;
	var cbIndex = -1;

	for(var i=0;i<cbIds.length; i++){
		if(cbIds[i] == req.query.id){
			cbIndex = i;
			break;
		}
	}
	console.log(" cbIndex "+ cbIndex);
	if(cbIndex == -1){
		res.status(400).send({error:"Requested callback id does not exists"});
	}else{

		for(var i=0;i<respArr.length;i++){

			console.log(respArr[i]);
			if(respArr[i][req.query.id] != undefined){
				flag = true;
				var data = respArr[i];
				respArr.splice(i,1);
				//TODO: confirm with shwetha.. 
				if(req.query.id[0] == 'c'){
					cbIds.splice(cbIndex,1);
					data["Message"] = "Success";
					res.send(data);
					return;
				} else{
					if(data['Message']!=undefined){
						cbIds.splice(cbIndex,1);
			
					}
					console.log("Sent response "+ data);
					res.send(data);
					return;
				}
				

			}
		}
		if(!flag){
			res.send({"id":req.query.id, "Message":"Pending"});
		}
	}
});





exports.router = router;