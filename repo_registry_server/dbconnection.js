var exports = module.exports = {};

var gatewayList = [];
var MongoClient = require('mongodb').MongoClient;

exports.getSensorList = function(gatewayId, callback) {

	MongoClient.connect("mongodb://localhost:27017/sensor", function(err, db) {

		db.collection('test', function(err, collection){

		});
		var collection = db.collection('test');

		collection.find().toArray(function(err, items) {

			
			var sensorArr = items[0].Gateways.Gateway;
			var len = sensorArr.length;
			//console.log(sensorArr);
			for(var i=0; i<len; i++){

				if(sensorArr[i].HWID == (gatewayId)){
					
					callback(sensorArr[i]);
				}
			}
			//res.send("Gateway Id not found!!");
		});
	});

};

exports.insertInitialVals = function(ids) {
	MongoClient.connect("mongodb://localhost:27017/sensor", function(err, db) {
		console.log('Inserting initial vals');
		var collection = db.collection('pingstatus');
		collection.remove({});
		var jsonObj = {};//'{filter:inactive}';//"{"+server.toString() +":"+ status.toString() +"}";
		jsonObj['device'] = 'filter'
		jsonObj['status'] = 'inactive';
		collection.insert(jsonObj);
		var len = ids.length;
		for(var i=0; i<len; i++){

			var jsonObj = {};
			jsonObj['device'] = ids[i][0];
			jsonObj['status'] = 'inactive';
			collection.insert(jsonObj);
		}
		db.close();
	});
}


exports.readXMLFile = function() {

	var hwids = [];
	var fs = require('fs');
	var data = fs.readFileSync('../repository.xml','utf8');

	var parseString = require('xml2js').parseString;
	var xml = data;
	var obj;
	parseString(xml, function (err, result) {
		json = JSON.stringify(result);
		obj = JSON.parse(json);
	});

	MongoClient.connect("mongodb://localhost:27017/sensorData", function(err, db) {

		db.collection('test', function(err, collection) {
			collection.remove();
		});
		var collection = db.collection('gateways');
		collection.insert(obj);
		//console.log(obj["Gateways"]["Gateway"][0]["HWID"]);
		var gatewayArr = obj["Gateways"]["Gateway"];
		var len = gatewayArr.length;
		for(var i=0; i<len; i++){
			var id = gatewayArr[i]["HWID"];
			hwids.push(id);
			gatewayList.push(id);
			var sensorArr = gatewayArr[i]["Sensor"];

			for(var j=0; j<sensorArr.length; j++){

				var idSensor = sensorArr[j]["ID"];
				hwids.push(idSensor);
			}
		}
		exports.insertInitialVals(hwids);
		db.close();
	});
};

exports.insertFSPingStatus = function(server,status) {
	MongoClient.connect("mongodb://localhost:27017/sensor", function(err, db) {

		var collection = db.collection('pingstatus');
		var jsonObj = {};//'{filter:inactive}';//"{"+server.toString() +":"+ status.toString() +"}";
		jsonObj['device'] = server.toString();
		jsonObj['status'] = status.toString();
		//collection.insert(jsonObj);

		collection.update(
			{'device':'filter'},
				{
					$set:
					{
						'status':status.toString()
					}
				}
			);
		db.close();
	});
};

exports.gatewayList = gatewayList;

