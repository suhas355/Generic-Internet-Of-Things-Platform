var exports = module.exports = {};

var express = require('express');
var router = express.Router();
var https = require('https');

var requestify = require('requestify');
router.route('/').post(function(req, res) {
	
	requestify.post('http://localhost:5000/getdata/registercallback/sensors', req.body)
		.then(function(response) {
    	// Get the response body (JSON parsed or jQuery object for XMLs)
    	console.log("Response from filter server " + response.getBody()); 
    	res.send(response.getBody());
	});
    
	
});

router.route('/getresults').get(function(req, res){

	var callbackId = req.query.cid;
	
})

exports.router = router;