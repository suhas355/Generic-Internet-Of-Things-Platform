var express = require('express');
var requestify = require('requestify');

var ip = '10.42.0.19';

var db = require("./graph");
var type = 'traffic';
var location = 'Hyderabad';

var path = require('path');
var lssocket = require('./lssocket')
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var routes = require('./routes/index');
var users = require('./routes/users');
var lsrouter = require('./routes/lsrouter');
var app = express();

var graph = require('./graph')

var mongoose = require('mongoose');

var dbName = 'graph';
var connectionString = 'mongodb://localhost:27017/' + dbName;
mongoose.connect(connectionString);
//graph.readGraph();
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);
app.use('/getlocation',lsrouter.router);
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



app.set('port', process.env.PORT || 6010);
 
var server = app.listen(app.get('port'), function() {
  console.log('Express server listening on port ' + server.address().port);
  lsrouter.init();
});



module.exports = app;
