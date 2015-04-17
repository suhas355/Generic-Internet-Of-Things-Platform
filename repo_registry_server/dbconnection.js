var exports = module.exports = {};

var devicedb = require('./model/dbschema');

var gatewayList = [];

exports.clearDb = function(){ 
	devicedb.sensorinfo.remove({},function(err){
		if(err){
			console.log('Error: '+err);
		}
	});

	devicedb.gatewayinfo.remove({},function(err){
		if(err){
			console.log('Error: '+err);
		}
	});

	devicedb.pingstatus.remove({},function(err,status){
		if(err){
			console.log('Error in removing entries..');
		}
	});
}

exports.readXMLFile = function() {
	exports.clearDb();
	exports.insertInitialVals('filter');
	var fs = require('fs');
	var data = fs.readFileSync('../repository.xml','utf8');

	var parseString = require('xml2js').parseString;
	var xml = data;
	var obj;
	parseString(xml, function (err, result) {
		json = JSON.stringify(result);
		obj = JSON.parse(json);
	});


	var gatewayArr = obj['Gateways']['Gateway'];
	var len = gatewayArr.length;
	for(var i=0; i<len; i++){

		/************** Gateway data **************/
		var geoloc =  gatewayArr[i]['GeoLocation'];
		var latitude = geoloc[0]['Latitude'];
		var longitude =  geoloc[0]['Longitude'];

		var jsonObj = {};
		var mac = gatewayArr[i]['MacAddress']
		gatewayList.push(mac);
		exports.insertInitialVals('G'+mac);
		jsonObj['macAddress'] = mac[0];
		jsonObj['location'] = gatewayArr[i]['LocationName'][0];
		jsonObj['geo'] = [ Number(latitude[0]) , Number(longitude[0])];

		console.log(jsonObj);
		var gatewayData = new devicedb.gatewayinfo(jsonObj);

		gatewayData.save(function(err) {
		    if (err) {
		     	console.log('Error in gatewayData insertion ' + err);
		    }
	  	});


	  	/***************Sensor data **************/
	  	var sensorArr = gatewayArr[i]["Sensor"];
	  	var slen = sensorArr.length;

	  	var sensorJson = {};
	  	for(var j=0; j<slen; j++){
	  		exports.insertInitialVals('S'+sensorArr[j]['ID']);
	  		sensorJson['sensorId'] = sensorArr[j]['ID'][0];
	  		sensorJson['gatewayId'] = mac[0];
	  		sensorJson['deviceName'] = sensorArr[j]['SensorDevice'][0];
	  		sensorJson['type'] = sensorArr[j]['Type'][0];
	  		sensorJson['unit'] = sensorArr[j]['Unit'][0];
	  		sensorJson['location'] = sensorArr[j]['SensorLocationName'][0];
	  		var slat = sensorArr[j]['SensorGeoLocation'][0]['Latitude'];
	  		var slong = sensorArr[j]['SensorGeoLocation'][0]['Longitude'];
	  		var arr = [];
	  		arr.push(Number(slong[0]));
	  		arr.push(Number(slat[0]));
	  		sensorJson['geo'] = arr;
	  		sensorJson['altitude'] = sensorArr[j]['Altitude'][0];
	  		sensorJson['protocol'] = sensorArr[j]['Protocol'][0];
	  		console.log(JSON.stringify(sensorJson));
	  		var sensorData = new devicedb.sensorinfo(sensorJson);

			sensorData.save(function(err) {
			    if (err) {
			      console.log('Error in sensor data insertion ' + err)
			    }
	  		});


 	  	}
	}
}

exports.insertInitialVals = function(device) {
	console.log('Inserting initial vals for ---' + device + "------");
	
	var jsonObj = {};
	jsonObj['device'] = device;
	jsonObj['status'] = 'inactive';
	var statusdata = new devicedb.pingstatus(jsonObj);
	statusdata.save(function(err){
		if(err){
			console.log('Error in putting initial status ' + device);
		}
	});
}

exports.insertFSPingStatus = function(server,status) {
	var jsonObj = {};

	devicedb.pingstatus.findOne(
		{'device':server.toString()}
		,function(err,device){
			if(err){
				console.log('Error in finding entry for ' + server);
			}else{
				console.log('server ' + server + ' status ' + status)
				device['status'] = status;
				device.save(function(err){
					if(err){
						console.log('Error in inserting status');
					}
				});
			}
		}
	);
}

exports.getSensorList = function(gatewayId, callback) {

	devicedb.sensorinfo.find({'gatewayId':gatewayId},
		function(err,sinfo){
			console.log(sinfo);
			if(err){
				console.log('Error in getting sensor info for gateway ' +gatewayId)
				console.log(err)
			}else{
				callback(sinfo);
			}
			console.log(sinfo);
	});
}

exports.gatewayList = gatewayList;