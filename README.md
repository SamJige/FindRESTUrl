# FindRESTUrl

Search Request URLs(something like @GetMapping(value = "xxxx") ) in Spring Web App  

Use Case: I want to debug a URL in my project bug not know exactly which file the RequestMapping is
   and maybe I cannot find the code when I use "find in path" with the URL string because Controller and Method build it together. 

Then I can use this plugin now 

function similar to RestfulToolkit  

code is open source  

1. use `ctrl+\` key open the search dialog  
2. input url fragment to search where the code are. string in clipboard will be pasted into text input automatically  
3. click the search result will navigate to the code  

----

搜索java代码里面的REST URL  

使用场景: 我知道某个URL被调用了 但是不知道具体实现代码在哪里
   因为URL可能是controller和method里面的mapping拼接的 找起来很麻烦 这时候就可以用这个插件进行快速搜索  

功能跟RestfulToolkit类似  

代码是开源的  

1. 使用 `ctrl+\` 快捷键弹出搜索框  
2. 输入要搜索的URL片段 剪贴板有值可以直接进入搜索框  
3. 点击搜索结果跳转到对应的代码  