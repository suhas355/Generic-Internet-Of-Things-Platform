var exports = module.exports = {};
var filterapi = require('./models/dbschema');
var router = require('./routes/filterapi');
var io = require('socket.io-client');
var socketPort = "3550";
var getQuery = function(object){

	var query = filterapi.find();
	var conditionArr = [];
	if(object['sensorId']!= undefined ){

		query = query.where('sensorId').in(object['sensorId'].toString().split(","));
	
	}

	if(object['minValue']!=undefined){

		var min = parseFloat(object['minValue']);

		var json = {};
		var cond1 = {};
		cond1['$lte'] = min;
		json['data'] = cond1;
		
		conditionArr.push(json);
	}

	if(object['maxValue']!=undefined){

		var max = parseFloat(object['maxValue']);

		var json = {};
		var cond2 = {};
		cond2['$gte'] = max;
		json['data'] = cond2;
		
		conditionArr.push(json);
	}

	if(conditionArr.length > 0){
		query = query.or(conditionArr);
		console.log(conditionArr);
	}

	if(object['location']!=undefined){
		query = query.where('location').equals(object['location']);
	}

	return query;
}

var executeQueries = function(){

	//console.log("Exx.. queries");

	var queryMapping = router.queryMapping;
	for(var key in queryMapping){

		var query = queryMapping[key]["query"];
		var ip = queryMapping[key]["IP"];
		var time = queryMapping[key]["time"];
		var currentTime = new Date()/1000;

		if(time < currentTime){

			sendResponse(key,{"Message": "No data found for the request"}, ip);

		} else {
		//console.log(query);
			query.exec(function(err, sensordata){

				if(err){
					console.log(err);
				} else{
					if(sensordata.length == 0){
						console.log("empty");
					} else{
						sendResponse(key,sensordata, ip);
					}
				}
			});
		}
	}
}

var sendResponse = function(key, sensordata, ipaddress){
	console.log("sendResponse called "+ key + ": " + sensordata + "::" + ipaddress);
	//1. Create socket
	var queryMapping = router.queryMapping;
	delete queryMapping[key];
	var index = ipaddress.lastIndexOf(":");
	var url = "";
	if(index!=-1){
		url = ipaddress.substring(index+1);
	} else {
		url = ipaddress;
	}
	
	url = "http://"+url + ":" + socketPort;
	var socket = io.connect(url);
	console.log("URL:" + url);
	var response = {};
	response[key] = sensordata;
	socket.emit('Callback Response', response);

	//console.log(queryMapping.)
}

exports.executeQueries = executeQueries;
exports.getQuery = getQuery;
