/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */


import React, { Component } from 'react';
import {
    ScrollView,
  AppRegistry,
  StyleSheet,
  Text,
  View,
Image
} from 'react-native';

export default class Iyangcong extends Component {
    constructor(){
        super();
        this.state = {title: "welcome"};
    }
  render() {
    return (
            <View style={styles.container}>
            
            <ScrollView  style={styles.scrollView}>
            <Text style={styles.question}>1.  如何调整亮度、字体大小、背景色和翻页模式？</Text>
            <Text style={styles.answer}>在阅读器内，点击屏幕中部。</Text>
            <Image source={require('./react_resource/images/reader1.png')} style={styles.base}/>
            <Text style={styles.answer}>屏幕底部将出现图标。</Text>
            <Image source={require('./react_resource/images/reader2.png')} style={styles.base}/>
            <Text style={styles.answer} >点击T形图标，在弹出的浮层中，可设置亮度、字号、翻页、背景和夜间模式。</Text>
            <Image source={require('./react_resource/images/reader3.png')} style={styles.base}/>
            <Text style={styles.question}>2.  如何从书架删除图书？</Text>
            <Text style={styles.answer}>点击书架右上角的铅笔图标，进入编辑模式。</Text>
            <Text style={styles.answer}>点击封面选中该书。</Text>
            <Text style={styles.answer}>点击左下方“删除”按钮，从书架删除图书。</Text>
            <Text style={styles.answer}>点击右上角的对号图标，退出编辑模式。</Text>
            
            <Text style={styles.question}>3.  已购买图书，从书架删除后，还能找回来吗？</Text>
            <Text style={styles.answer}>可以找回来，不需要再次购买。来到书架，点击左上角云朵图标，来到云书架，搜索您想找回的图书，下载后即可阅读。网站上购买的书，也可用这种方法找回。</Text>
            <Text style={styles.question}> 4.  图书打开时，提示解压错误，怎么办？</Text>
            <Text style={styles.answer}>请尝试从书架删除该书，重新下载。如还不能解决问题，请到我的-意见反馈，说明问题，留下正确的联系方式，爱洋葱小编将联系您，协助解决该问题。</Text>
            <Text style={styles.question}> 5.  充值后，资金未到账，怎么办？</Text>
            <Text style={styles.answer}>充值后，支付平台若未能及时返回交易成功的信息，可能会导致该问题。遇到这种情况，请保留好充值的凭证（交易记录截图），记好交易时间。请到我的-意见反馈，说明问题，留下正确的联系方式，爱洋葱小编将联系您，协助解决该问题。</Text>
            <Text style={styles.question}>6.  如何兑换图书？</Text>
            <Text style={styles.answer}>爱洋葱官方做活动、答谢用户时，会向用户发放兑换码。兑换码是一串数字。拿到兑换码后，请到我的-兑换图书，输入兑换码兑换。兑换图书前，请确保您已登录爱洋葱阅读。</Text>
            <Text style={styles.question}>7.  如何修改密码？</Text>
            <Text style={styles.answer}>点击应用底部“我的”，打开“我的”页面，点击头像，打开个人设置页面。修改密码的入口在个人设置的最底部。</Text>
            <Text style={styles.question}> 8.  如何知道我安装的是最新版吗？</Text>
            <Text style={styles.answer}> 我的-更多设置-检查更新。</Text>
            <Text style={styles.question}> 9.  如何知道我安装的是哪个版本？</Text>
            <Text style={styles.answer}>我的-更多-关于产品。</Text>
            
            </ScrollView>
            </View>
    );
  }
}

const styles = StyleSheet.create({
                                 container: {
                                 flex: 1,
                                 justifyContent: 'center',
                                 alignItems: 'center',
                                 
                                 },
                                 
                                 instructions: {
                                 textAlign: 'center',
                                 color: '#333333',
                                 marginBottom: 5,
                                 },
                                 base: {
                                 width: 350,
                                 height: 500,
                                 flex: 1,
                                 resizeMode: Image.resizeMode.stretch,
                                 marginLeft:12,
                                 },
                                 scrollView: {
                                 height: 300,
                                 },
                                 question:{
                                 fontWeight: 'bold',
                                 padding: 10,
                                 marginLeft:15,
                                 marginRight:15,
                                 },
                                 answer:{
                                 padding: 5,
                                 marginLeft:35,
                                 marginRight:15,
                                 }

});

AppRegistry.registerComponent('Iyangcong', () => Iyangcong);
