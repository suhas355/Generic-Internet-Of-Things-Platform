var express = require('express');
var db = require("./dbconnection.js");
db.readXMLFile();


var repo_reg = require("./registrysocket.js");
setInterval(repo_reg.pingFilterServer,3000);
setInterval(repo_reg.pingGateway, 3000);
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');


var routes = require('./routes/index');
var users = require('./routes/users');


var mongoose = require('mongoose');
var dbinfo = require('./routes/sensorinfo'); 


var app = express();

var dbName = 'sensordata';
var connectionString = 'mongodb://localhost:27017/' + dbName;
 
mongoose.connect(connectionString);



// view engine setup


app.get('/getSensors', function (req, res) {
	var id = req.query.hwid;
  db.getSensorList(id, res);
});

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/sensor',dbinfo);
app.use('/', routes);
app.use('/users', users);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


app.listen(3000);
