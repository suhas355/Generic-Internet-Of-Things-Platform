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
		jsonObj['macAddress'] = mac;
		jsonObj['location'] = gatewayArr[i]['LocationName'];
		var latjson = {};
		latjson['latitude'] = latitude.toString();
		var longjson = {};
		longjson['longitude'] = longitude.toString();
		jsonObj['geolocation'] = [ latjson , longjson];


		var gatewayData = new devicedb.gatewayinfo(jsonObj);

		gatewayData.save(function(err) {
		    if (err) {
		     	console.log('Error in gatewayData insertion');
		    }
	  	});


	  	/***************Sensor data **************/
	  	var sensorArr = gatewayArr[i]["Sensor"];
	  	var slen = sensorArr.length;

	  	var sensorJson = {};
	  	for(var j=0; j<len; j++){
	  		exports.insertInitialVals('S'+sensorArr[j]['ID']);
	  		sensorJson['sensorId'] = sensorArr[j]['ID'];
	  		sensorJson['gatewayId'] = mac;
	  		sensorJson['deviceName'] = sensorArr[j]['SensorDevice'];
	  		sensorJson['type'] = sensorArr[j]['Type'];
	  		sensorJson['unit'] = sensorArr[j]['Unit'];
	  		sensorJson['location'] = sensorArr[j]['SensorLocationName'];
	  		var slat = {};
	  		slat['latitude'] = sensorArr[j]['SensorGeoLocation'][0]['Latitude'].toString();
	  		var slong = {};
	  		slong['longitude'] = sensorArr[j]['SensorGeoLocation'][0]['Longitude'].toString();
	  		sensorJson['geolocation'] = [ slat,slong];
	  		sensorJson['protocol'] = sensorArr[j]['Protocol'];
	  	//	sensorData['pullfrequency'] = ;

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
	});
}

exports.gatewayList = gatewayList;