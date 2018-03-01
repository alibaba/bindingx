## ReadMe

1. here is only one html using SPA. but pc and mobile are two different page(js).
2. index.html is for common use, while 404.html is for url with path (like refresh in docs page).
3. version param is only controls PC page version.
4. mobile page version is just on the url path.

pc: git@gitlab.alibaba-inc.com:bindingx/site-assets.git
mobile: git@gitlab.alibaba-inc.com:bindingx/mobile-site-assets.git


## change PC site:

* Step1: fork code from gitlab.
* Step2: develop. change branch to daily/0.x.x. use `tnpm start` for dev. your can visit the pc site by 'https://localhost:8010/'
* Step3: start http-server at gh_pages folder. also view page by 'https://localhost:8080/'
* Step4: publish. use `def p -d` & `def p -o`.
* Step5: change index.html & 404.html version to 0.x.x. and push to gh_pages branch.


## change mobile site:

* Step1: fork code from gitlab.
* Step2: develop. using `webpack --config webpack.dev.js`, then start a http-server at build folder. `http-server -p 9112`. now your can access the target file by 'http://localhost:9112/home_bundle.js'. Another http-server at this gb_pages folder. `http-server -p 9111`, then visit 'https://localhost:9111/', turn on devtools panel and toggle device toolbar. change to mobile mode.
* Step3: publish. run build by command `webpack --config webpack.build.js` target JS file is in build folder.
* Step4: use freedom - download publish to send the file to cdn. do not forget rename the js file with version name, like bindingx_mobile_home_0.2.js.
* Step5: copy cdn path to gh_pages file. inculde index.html & 404.html. like 
``` 
loadScript(`//download.alicdn.com/sns/bindingx_mobile_home_0.2.js?t=${now}`);
```
