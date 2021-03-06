var exports = module.exports = {};

var lssoc = require('../monitor_socket');

var ip = 'localhost';//"192.168.217.106";

var express = require('express');
var router = express.Router();
var https = require('https');
var cbIds = [];
var requestify = require('requestify');


/**
JSON structure

{
	'location' : 'amb-himagiri',
	'type' : ['ecg','bp','highpulse']
}

*/

router.route('/getsensors').post(function(req, res){
	console.log("Recieved request for get sensors " + JSON.stringify(req.body));
	requestify.post('http://'+ip+':3000/sensor/getsensors', req.body)
		.then(function(response) {
				console.log("Response from registry" + response.getBody()); 
				var sensorArr = response.getBody();
				var resMap = {}
				for(var i=0;i<sensorArr.length;i++){
					resMap[sensorArr[i]['type']] = sensorArr[i]['sensorId'];
				}
				console.log('response ' + JSON.stringify(resMap));
		    	res.send(resMap);
		   
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
	//console.log(" cbIndex "+ cbIndex);
	if(cbIndex == -1){
		res.status(400).send({'Message':'Failure',error:"Requested callback id does not exists"});
	}else{

		for(var i=0;i<respArr.length;i++){

			//console.log(respArr[i]);
			if(respArr[i][req.query.id] != undefined){
				flag = true;
				var data = respArr[i];
				respArr.splice(i,1);
				//TODO: confirm with shwetha.. 
				if(req.query.id[0] == 'c'){
					//cbIds.splice(cbIndex,1);
					var resp = {}
					resp[data[req.query.id][0]['type']] = data[req.query.id][0]['data'];
					resp['Message'] = 'Success';
					//data["Message"] = "Success";
					console.log('Sent response  for callback id' + req.query.id + ' value '+ resp);
					res.send(resp);
					return;
				} else{
					var resp = {};
					if(data['Message']!=undefined){
						cbIds.splice(cbIndex,1);
						resp['Message'] = data['Message'];
			
					}else{
						resp['Message'] = 'Success';
					}					
					var dataArr = data[req.query.id];
					for(var i=0;i<dataArr.length;i++){
						resp[dataArr[i]['type']] = dataArr[i]['data'];

					}
					//console.log("Sent response for freq data " + req.query.id+ ' value '+ resp);
					res.send(resp);
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