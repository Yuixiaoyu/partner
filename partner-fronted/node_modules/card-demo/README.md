# card-demo

## 快速开始

#### 1、安装组件库
```bash
npm install card-demo
```

#### 2、引用组件库 
#####组件引入
```javascript
import "card-demo/dist/css/index.css" 
import Card from 'card-demo/dist/card.umd'
import Demo from 'card-demo/dist/demo.umd'
```
#####Card组件使用
```haml 
1.样式一
<Card imgSrc="1.jpg"
       summary="很多设计师喜欢用英文，因为中文排版真的不太容易搞。中文字符多、不同字符的笔画差异也很大，使用英文时高大上的设计稿，替换成中文以后，可能会大打折扣。"
 />

2.样式二
<Card imgSrc="2.jpg"
      summary="网页设计中，针对中文排版的研究很少，没有太多现成的结论供参考。Amaze UI 只能根据一些经验，在字体设置、字号上做一些更适合中文的设置。"
 >
     <template v-slot:footer>
         <div class="footer">
             <div class="level">中级·888人报名</div>
             <div class="price">¥299.00</div>
         </div>
     </template>
</Card>

3.样式三
<Card
         imgSrc="3.jpg"
         :width="379"
         :height="90"
 >
     无论走到哪里，都应该记住，过去都是假的，回忆是一条没有尽头的路，一切以往的春天都不复存在，就连那最坚韧而又狂乱的爱情归根结底也不过是一种转瞬即逝的现实。
     <template v-slot:footer>
         <div class="footer-spring">
             <div class="level">4步骤·6门课</div>
             <div class="level">10965收藏</div>
         </div>
     </template>
 </Card>
```
#####Demo组件使用
```html
    <Demo />
```
 
 
