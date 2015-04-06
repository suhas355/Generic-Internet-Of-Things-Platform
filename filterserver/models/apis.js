var mongoose=require('mongoose');
var Schema=mongoose.Schema;
 
var sensorDataSchema = new Schema({
  sensorId: String,
  type: String,
  unit: String,
  location: String,
  data: String,
  timestamp: { type : Date, default: Date.now }
});
 
module.exports = mongoose.model('filterapi', sensorDataSchema);