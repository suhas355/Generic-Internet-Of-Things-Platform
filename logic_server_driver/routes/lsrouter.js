var exports = module.exports = {};

var lssoc = require('../lssocket');
var graphDB = require('../models/graphschema')
var graphs = require('../graph')
var ip = "127.0.0.1";

var express = require('express');
var router = express.Router();
var https = require('https');
var cbIds = [];
var destinationdata = [];
var pathTraversed = [];
var requestify = require('requestify');
router.route('/nextlocation').post(function(req, res) {
	
	console.log("Recieved request for register callback");

	var sensorIds = [];
	var jsonReq = {}
	for(var i=0; i<30; i++){
		sensorIds.push(i.toString());
	}
	console.log(sensorIds);
	var source = req.body.source;
	jsonReq["sensorId"] = sensorIds;
	requestify.post('http://'+ip+':5000/getdata/getsensordata', jsonReq)
		.then(function(response) {
				//console.log("Response from filter server " + JSON.stringify(response.getBody())); 
				var len = response.getBody().length;
				var heatMap = [];
				for(var i=0; i<len; i++){
					var sid = response.getBody()[i].sensorId;
					console.log(sid + " i:" + i);
					var nsid = parseInt(sid);
					heatMap[nsid] = response.getBody()[i]['data'];
				}
		    	graphs.findNextLocation(heatMap, res, source);

		   
	},
	function(err){
		if(err){
			console.log('Error ' + err);
			res.status(err.getCode()).send(err.getBody());
		}
	});
	
	

});

router.route('/getresults').get(function(req, res){
	
});


exports.router = router;