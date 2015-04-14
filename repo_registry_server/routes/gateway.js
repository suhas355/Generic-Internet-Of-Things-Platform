var exports = module.exports = {};

var info = require('../model/dbschema');

var express = require('express');
var router = express.Router();




router.route('/').get(function(req,res){
  res.sendfile('./public/html/gateway.html');
});


module.exports = router;