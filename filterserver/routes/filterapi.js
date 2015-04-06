var exports = module.exports = {};

var filterapi = require('../models/apis');
var express = require('express');
var router = express.Router();

router.route('/getdata').get(function(req, res) {
	console.log(req.query);
  filterapi.find(
  	{sensorId : req.query.sensorId},
  	function(err, sensordata) {
    if (err) {
      return res.send(err);
    }
 
    res.json(sensordata);
  });
});

router.route('/sensordata').post(function(req, res) {
	console.log(req.body);
  var sensordata = new filterapi(req.body);
 
  sensordata.save(function(err) {
    if (err) {
      return res.send(err);
    }
 
    res.send({ message: 'Data Added' });
  });
});

module.exports = router;