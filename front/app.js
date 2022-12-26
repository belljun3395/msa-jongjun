// npm
import createError from 'http-errors';
import express from 'express';
import nunjucks from 'nunjucks';
import path from 'path';
import morgan from 'morgan';
import { fileURLToPath } from 'url';
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
import cookieParser from 'cookie-parser';
import session from 'express-session';
import bodyParser from 'body-parser';
import dotenv from 'dotenv';

// router
import indexRouter from './routes/index.js';

// config
dotenv.config();

// express start
const app = express();

// port
app.set('httpPort', process.env.PORT || 3000);


// view engine setup
app.set('view engine', 'html');
nunjucks.configure(path.join(__dirname, '/public/views'), {
  express: app,
  watch: true
});
app.use(morgan('dev'));
app.use(bodyParser.json())
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, '/public')));
app.use(cookieParser());

const sessionOption = {
  resave: false,
  saveUninitialized: false,
  secret: process.env.SESSION_SECRET,
  cookie: {
    httpOnly: true,
    secure: false,
  },
};

app.use(session(sessionOption));

// router
app.use('/', indexRouter);

// catch 404 and forward to error handler
app.use((req,res,next) => {
  const err = new Error('NotFound');
  err.status=404;
  logger.info('hello');
  logger.error(err.message);
  next(err);
});

app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  res.status(err.status || 500);
  res.render('error');
});


app.listen(app.get('httpPort'), () => {
  console.log(app.get('httpPort'), '번 포트에서 대기중');
});

export default app;
