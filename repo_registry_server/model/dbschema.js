var mongoose = require('mongoose');
var Schema = mongoose.Schema;


var sensorInfoSchema = new Schema({
	sensorId: String,
	gatewayId: String,
  deviceName : String,
  type: String,
  unit: String,
  location : String,
  geo: { type: [Number], index: '2d' },
  altitude : Number,
  protocol: String
});

var sensorinfo = mongoose.model('sensorinfo', sensorInfoSchema);

var gatewayInfoSchema = new Schema({
	macAddress : String,
	location : String,
  geo: { type: [Number], index: '2d' },
});

var gatewayinfo = mongoose.model('gatewayinfo', gatewayInfoSchema);

var pingstatusSchema = new Schema({
  device : {type:String, required : true},
  status : {type: String,required : true}
});

var pingstatus = mongoose.model('pingstatus',pingstatusSchema);

module.exports = {
  sensorinfo : sensorinfo,
  gatewayinfo : gatewayinfo,
  pingstatus : pingstatus
};