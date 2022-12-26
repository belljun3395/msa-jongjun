import { Router } from "express";
var router = Router();

router.get('/', function(req, res, next) {
  res.render('home');
});

router.get('/login', function(req, res, next) {
  res.render('login');
});

router.get('/register', function(req, res, next) {
  res.render('register');
});

router.get('/user', function(req, res, next) {
  res.render('user');
});

router.get('/group', function(req, res, next) {
  res.render('group');
});

router.get('/mypage', function(req, res, next) {
  res.render('mypage');
});

router.get('/group/info', function(req, res, next) {
  res.render('groupInfo');
});

export default router