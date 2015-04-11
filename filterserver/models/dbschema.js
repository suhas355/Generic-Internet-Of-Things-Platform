var mongoose=require('mongoose');
var Schema=mongoose.Schema;
 
var sensorDataSchema = new Schema({
  sensorId: {type:String, required:true, unique:true},
  type: String,
  unit: String,
  location : String,
  geolocation: [{latitude : Number, longitude : Number}],
  data: {type: Number, required: true},
  timestamp: { type : Date, default: Date.now }
});
 
module.exports = mongoose.model('filterapi', sensorDataSchema); 