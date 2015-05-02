var WebSocketServer = require("ws").Server
var http = require("http")
var express = require("express")
var app = express()
var port = process.env.PORT || 5000
var pg = require('pg');

app.use(express.static(__dirname + "/"))

var server = http.createServer(app)
server.listen(port)

console.log("http server listening on %d", port)

var wss = new WebSocketServer({server: server})
console.log("websocket server created")

var baseClient;
pg.connect(process.env.DATABASE_URL, function(err, client) {
	baseClient = client;
});
var that = this;
wss.on("connection", function(ws) {
	// console.log(ws)
  	var id = setInterval(function() {
		var query = baseClient.query('SELECT * FROM user_location');
		query.on('row', function(row) {
			row.time = parseTwitterDate(row.time);
			ws.send('[' + JSON.stringify(row) + ']');
			// ws.send('[' + JSON.stringify(row) + ']');
		});
		// ws.send('empty', function() {  })
  	}, 4000)

  	console.log("websocket connection open")

  	ws.on("close", function() {
    	console.log("websocket connection close")
    	clearInterval(id)
  	})
	ws.on("message", function(message) {
	  	var obj = JSON.parse(message);
	  	var tempQuery = "UPDATE user_location SET name ='" + obj.name + "', time='" + new Date() + "', lat='" + obj.lat + "', long='" + obj.long + "'  where uniqueid='" + obj.uniqueid + "';";
	  	tempQuery += "insert INTO user_location(name, uniqueid, lat, long, time) select '" + obj.name + "','" + obj.uniqueid + "','" + obj.lat + "','" + obj.long + "','" + new Date() + "' where not exists (select 1 from user_location where uniqueid = '" + obj.uniqueid + "');";
	  	var query = baseClient.query(tempQuery);
		console.log(tempQuery)
	});


	x1=28.609882
	y1=77.057798
	x2=28.752048
	y2=77.050932
	x3=28.696650
	y3=77.262419
	x4=28.617115
	y4=77.196501
	x5=28.517009
	y5=77.173155
	x6=28.571295
	y6=77.153929

	function check(){
		var d1 = Math.random()/10000;
		var d2 = Math.random()/10000;
		var d3 = Math.random()/10000;
		var d4 = Math.random()/10000;
		var d5 = Math.random()/10000;
		var d6 = Math.random()/10000;
		x = new Date();
		x1 = newValueForX(x1,d1);
		x2 = newValueForX(x2,d2);
		x3 = newValueForX(x3,d3);
		x4 = newValueForX(x4,d4);
		x5 = newValueForX(x5,d5);
		x6 = newValueForX(x6,d6);

		y1 = newValueForY(y1,d1);
		y2 = newValueForY(y2,d2);
		y3 = newValueForY(y3,d3);
		y4 = newValueForY(y4,d4);
		y5 = newValueForY(y5,d5);
		y6 = newValueForY(y6,d6);

		updateBotLocation(d1, x1, y1, "asd");
		updateBotLocation(d2, x2, y2, "asdf");
		updateBotLocation(d3, x3, y3, "haha");
		updateBotLocation(d4, x4, y4, "haha2");
		updateBotLocation(d5, x5 ,y5, "haha3");
		updateBotLocation(d6, x6 ,y6, "abcd");
	    setTimeout(check, 20 * 1000);
	}
	check();

	function updateBotLocation(d, lat, long, uniqueid){
			var tempQuery = "UPDATE user_location SET time='" + new Date() + "', lat='" + lat + "', long='" + long + "'  where uniqueid='" + uniqueid + "';";
			var query = baseClient.query(tempQuery);
	}

	function newValueForX(x, d){
		if(Math.floor((d * 100000) + 1) == 9){
			return x + d;
		}else if(Math.floor((d * 100000) + 1) > 9){
			return x - d;
		}else{
			return x;
		}
	}

	function newValueForY(y, d){
		if(Math.floor((d * 100000) + 1) == 9){
			return y + d;
		}else if(Math.floor((d * 100000) + 1) > 9){
			return y;
		}else{
			return y - d;
		}
	}
})

function parseTwitterDate(tdate) {
    var system_date = new Date(Date.parse(tdate));
    var user_date = new Date();
    var diff = Math.floor((user_date - system_date) / 1000);
    if (diff <= 1) {return "just now";}
    if (diff < 20) {return diff + " seconds ago";}
    if (diff < 40) {return "half a minute ago";}
    if (diff < 60) {return "less than a minute ago";}
    if (diff <= 90) {return "one minute ago";}
    if (diff <= 3540) {return Math.round(diff / 60) + " minutes ago";}
    if (diff <= 5400) {return "1 hour ago";}
    if (diff <= 86400) {return Math.round(diff / 3600) + " hours ago";}
    if (diff <= 129600) {return "1 day ago";}
    if (diff < 604800) {return Math.round(diff / 86400) + " days ago";}
    if (diff <= 777600) {return "1 week ago";}
    return "on " + system_date;
}
