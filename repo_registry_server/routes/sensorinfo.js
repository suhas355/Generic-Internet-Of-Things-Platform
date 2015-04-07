var exports = module.exports = {};

var info = require('../model/dbschema');
var express = require('express');
var router = express.Router();

//get sensor id based on location
router.route('/getsensors/location').post(function(req, res) {
	console.log(req.body.location);
  if(req.body.location == undefined){
    res.status(422).send({error:"Missing mandatory fields in JSON"});
  }else{
      
        info.sensorinfo.find({
              "location": req.body.location
            },
            function(err, sensordata) {
            if (err) {
              return res.send(err);
            }
               res.json(sensordata);
           
           }); 
            
            
    }
});

//get sensor id based on geo location



//get sensor id based on type

module.exports = router;