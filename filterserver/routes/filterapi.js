var exports = module.exports = {};

var filterapi = require('../models/dbschema');
var express = require('express');
var router = express.Router();

router.route('/getdata').get(function(req, res) {
	console.log(req.query);
  filterapi.find(
  	{sensorId : req.query.sensorId},
  	function(err, sensordata) {
    if (err) {
      return res.send("error in request");
    }
 
    res.json(sensordata);
  });
});

router.route('/getdata/geolocation').post(function(req, res) {
  console.log(req.body.latitude);
  console.log(req.body.longitude);
  if(req.body.radius == undefined){
    console.log('radius is undefined ');
    filterapi.find({
        $and: [
         {"location.latitude" : req.body.latitude},
         {"location.longitude": req.body.longitude}
        ]},
        function(err, sensordata) {
        if (err) {
          return res.send(err);
        }
           res.json(sensordata);
       
       });
     
    }
    else
    {
      console.log('radius is ' + req.body.radius);
      res.json({"good" : "ok"});
    }
   
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