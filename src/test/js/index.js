const app = require('express')(),
			bodyParser = require('body-parser'),
			basicAuth = require("basic-auth-connect");

app.use(bodyParser.urlencoded({extended: true}));
app.set('views', __dirname);
app.set('view engine', 'ejs');
app.all('/basic', basicAuth(function(user, pw){
	return user === 'u' && pw === 'p';
}));

app.get('/', function(req, res){
	const response = {
		qs: req.query.name,
		headers: req.headers,
		ip: req.ip
	};
  res.status(200).send(JSON.stringify(response));
});

app.post('/form', function(req, res){
  res.status(200).send(JSON.stringify(req.body));
});

app.get('/view', function(req, res) {
	res.render('view');
});
app.get('/basic', function(req, res) {
	res.render('view');
});

app.get('/slow', function(req, res) {
	setTimeout(function(){
  	res.status(200).send("You are patient enough");
	}, 5000);
});

app.get('/notfound', function(req, res) {
  res.status(404).send('sorry,');
});
app.listen(3000);
