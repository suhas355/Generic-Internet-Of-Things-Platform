var exports = module.exports = {};
var filterapi = require('./models/dbschema');
var router = require('./routes/filterapi');
var getQuery = function(object){

	var query = filterapi.find();
	var conditionArr = [];
	if(object['sensorId']!= undefined ){

		query = query.where('sensorId').in(object['sensorId'].toString().split(","));
	
	}

	if(object['minValue']!=undefined){

		var min = parseFloat(object['minValue']);

		var json = {};
		json['data'] = {$lte : min};
		
		conditionArr.push(json);
	}

	if(object['maxValue']!=undefined){

		var max = parseFloat(object['maxValue']);

		var json = {};
		json['data'] = {$gte : max};
		
		conditionArr.push(json);
	}

	if(conditionArr.length > 0){
		query = query.or(conditionArr);
		console.log(conditionArr);
	}

	return query;
}

var executeQueries = function(){

	console.log("Exx.. queries");

	var queryMapping = router.queryMapping;
	for(var key in queryMapping){

		var query = queryMapping[key]["query"];
		var ip = queryMapping[key]["IP"];
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

var sendResponse = function(key, sensordata, ipaddress){
	console.log("sendResponse called "+ key + ": " + sensordata + "::" + ipaddress);
	//1. Create socket
	var queryMapping = router.queryMapping;
	delete queryMapping[key];

	//console.log(queryMapping.)
}

exports.executeQueries = executeQueries;
exports.getQuery = getQuery;
