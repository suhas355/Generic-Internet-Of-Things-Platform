var exports = module.exports = {};

var lssoc = require('../lssocket');
var graphDB = require('../models/graphschema')
var graphs = require('../graph')
var ip = "127.0.0.1";

var express = require('express');
var router = express.Router();
var https = require('https');
var type = 'traffic';
var location = 'Hyderabad';

var cbIds = [];
var destinationdata = [];
var pathTraversed = [];
var requestify = require('requestify');
var sensors = [];
var locationMap = {};

exports.init = function(){
  console.log('Calling init');
  jsonReq = {};
  var tp = [];
  tp.push(type);
  jsonReq['type'] = tp;
  jsonReq['location'] = location;
  //jsonReq['location'] = 'Road0';
  requestify.post('http://'+ip+':3000/sensor/getsensors', jsonReq)
    .then(function(response) {
      sensors = response.getBody();
      //console.log("body : " +JSON.stringify(sensors)+  " len" + sensors.length);
     // console.log('Senssors : ' + JSON.stringify(sensors));
  });


   var fs = require('fs');
	var data = fs.readFileSync('./logic.xml','utf8');
	var parseString = require('xml2js').parseString;
	var xml = data;
	var obj;
	parseString(xml, function (err, result) {
		json = JSON.stringify(result);
		obj = JSON.parse(json);
		//console.log(obj)
	});

	var nodes = obj['nodes']['node'];
	for(var i=0; i<nodes.length; i++){

		var n = nodes[i]["area_no"]
		var geo = nodes[i]["geolocation"]
		locationMap[geo] = n;
	}

}

router.route('/nextlocation').post(function(req, res) {
	
	console.log("Recieved request for next location " + JSON.stringify(req.body));

	var sensorIds = [];
	var jsonReq = {}
	var slen = sensors.length;
	console.log('Slen ' + slen);
	for(var i=0; i<slen; i++){
		sensorIds.push(sensors[i]['sensorId']);//sarr[i]['sensorId']
	}
	var source = req.body.curloc; // curlocation
	var sourceN = locationMap[source];
	jsonReq["sensorId"] = sensorIds;
	//console.log('Json req  :' + JSON.stringify(jsonReq));
	requestify.post('http://'+ip+':5000/getdata/getsensordata', jsonReq)
		.then(function(response) {
				//console.log("Response from filter server " + JSON.stringify(response.getBody())); 
				var len = response.getBody().length;
				var heatMap = [];
				for(var i=0; i<len; i++){
					var sid = response.getBody()[i].sensorId;
					var nsid = parseInt(sid);
					heatMap[nsid] = response.getBody()[i]['data'];
				}
		    	graphs.findNextLocation(heatMap, res, sourceN);

		   
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