/*
 * Copyright (C) 2004-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

#include <cstring>
#include <stdio.h>
#include <algorithm>
#include <string>

#include <ZLFile.h>
#include <ZLInputStream.h>
#include <ZLStringUtil.h>
#include <ZLUnicodeUtil.h>

#include <ZLLogger.h>
#include <AndroidUtil.h>
#include <fstream>

#include "ZLAsynchronousInputStream.h"

#include "AES.h"
#include "MD5.h"

#include "ZLXMLReader.h"

#include "expat/ZLXMLReaderInternal.h"
#include <exception>

class ZLXMLReaderHandler : public ZLAsynchronousInputStream::Handler {

public:
    ZLXMLReaderHandler(ZLXMLReader &reader);

    void initialize(const char *encoding);

    void shutdown();

    bool handleBuffer(const char *data, std::size_t len);

private:
    ZLXMLReader &myReader;
};

ZLXMLReaderHandler::ZLXMLReaderHandler(ZLXMLReader &reader) : myReader(reader) {
}

void ZLXMLReaderHandler::initialize(const char *encoding) {
    myReader.initialize(encoding);
}

void ZLXMLReaderHandler::shutdown() {
    myReader.shutdown();
}

bool ZLXMLReaderHandler::handleBuffer(const char *data, std::size_t len) {
    return myReader.readFromBuffer(data, len);
}

static const std::size_t BUFFER_SIZE = 16384;//4096
//static const std::size_t BUFFER_SIZE = 2048;

void ZLXMLReader::startElementHandler(const char *, const char **) {
}

void ZLXMLReader::endElementHandler(const char *) {
}

void ZLXMLReader::characterDataHandler(const char *, std::size_t) {
}

const ZLXMLReader::nsMap &ZLXMLReader::namespaces() const {
    return *myNamespaces.back();
}

ZLXMLReader::ZLXMLReader(const char *encoding) {
    myInternalReader = new ZLXMLReaderInternal(*this, encoding);
    myParserBuffer = new char[BUFFER_SIZE];
    myTempParserBuffer = new char[BUFFER_SIZE];
}

ZLXMLReader::~ZLXMLReader() {
    delete[] myParserBuffer;
    delete[] myTempParserBuffer;
    delete myInternalReader;
}

bool ZLXMLReader::readDocument(const ZLFile &file) {
    return readDocument(file.inputStream());
}
static const void watchDog(JNIEnv *env){
    //用来查看虚拟机状态的一个方法
    jclass vm_class = env->FindClass("dalvik/system/VMDebug");
    jmethodID dump_mid = env->GetStaticMethodID( vm_class, "dumpReferenceTables", "()V" );
    env->CallStaticVoidMethod( vm_class, dump_mid );
    env->DeleteLocalRef(vm_class);
}
static const std::string callJavaMethod(JNIEnv *env, const char *str) {
    try{
//        ZLLogger::Instance().println("ZLXMLReader","刚开始调用的时候");//用来打印的测试语句，可以在测试的时候用
//        watchDog(env);//用来打印的测试语句，可以在测试的时候用
        ZLLogger::Instance().registerClass("ZLXMLReader");
        jclass clazz = env->FindClass("org/geometerplus/fbreader/formats/NativeFormatPlugin");
        if (clazz == NULL) {
            env->DeleteLocalRef(clazz);
            return NULL;
        }
//    jmethodID id = env->GetStaticMethodID(clazz, "methodCalledByJni","(Ljava/lang/String;)Ljava/lang/String;");//"(Ljava/lang/String;)V"
        jmethodID id = env->GetStaticMethodID(clazz, "methodCalledByJni",
                                              "(Ljava/lang/String;)[B");//"(Ljava/lang/String;)V"
        jstring msg = env->NewStringUTF(str);
//    jstring res = (jstring)env->CallStaticObjectMethod(clazz, id, msg);
//    if(res == NULL){
//        ZLLogger::Instance().println("wzp","callStaticObjectMethod return null");
//        return NULL;
//    }
//
        char *rtn = NULL;
//    jclass   clsstring   =   env->FindClass("java/lang/String");
//    const char* UTF_8 = "utf-8";
////    jstring   strencode   =   env->NewStringUTF("utf-8");
//    jstring   strencode   =   env->NewStringUTF(UTF_8);
//    jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
//    jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(res,mid,strencode);
        jbyteArray barr = (jbyteArray) env->CallStaticObjectMethod(clazz, id, msg);

        jsize alen = env->GetArrayLength(barr);
        jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
        if (alen >= 0) {
            rtn = (char *) malloc(alen + 1);//多申请的一个char长度的内存没有释放，会导致内存溢出；
//            rtn = (char *) malloc(alen);
            if(rtn == NULL){
                exit(1);
            }
            memcpy(rtn, ba, alen);
            rtn[alen] = 0;
        }
//        ZLLogger::Instance().println("ZLXMLReader", "0");
//        ZLLogger::Instance().println("ZLXMLReader","释放之前");//用来打印的测试语句，可以在测试的时候用
//        watchDog(env);//用来打印的测试语句，可以在测试的时候用
        env->ReleaseByteArrayElements(barr, ba, 0);
        env->DeleteLocalRef(barr);//释放数组的内存，否则容易占用jvm的内存，导致jvm内存溢出
//        env->ReleaseByteArrayElements(barr, ba, 0);
//        std::string stemp(rtn);
        std::string stemp(rtn,0,alen);
//        ZLLogger::Instance().println("ZLXMLReader", "java解密后的结果=" + stemp);

        free(rtn);
        rtn = NULL;//释放内存后把指针指向NULL，防止指针在后面不小心又被解引用了
        env->DeleteLocalRef(clazz);
        clazz = NULL;
//    env->DeleteLocalRef(res);
        env->DeleteLocalRef(msg);
        msg = NULL;
//    env->DeleteLocalRef(clsstring);
//    env->DeleteLocalRef(strencode);
        //std::string s = jstring2str(env, res);
//        ZLLogger::Instance().println("ZLXMLReader","释放hou");用来打印的测试语句，可以在测试的时候用
//        watchDog(env);用来打印的测试语句，可以在测试的时候用
        return stemp;
    }catch(std::bad_alloc & ba){
        ZLLogger::Instance().println("内存溢出了???：",ba.what());
    }
}



static const std::string jstring2str(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    env->DeleteLocalRef(clsstring);
    env->DeleteLocalRef(strencode);
    std::string stemp(rtn);
    free(rtn);
    return stemp;
}

static bool isEncrypting = false;

bool ZLXMLReader::readDocument(shared_ptr<ZLInputStream> stream) {
    static std::string startTag = "<start>";
    static std::string endTag = "</start>";
    static char *charsetWindows1252 = "windows-1252";
    if (stream.isNull() || !stream->open()) {
        return false;
    }
    std::size_t length;

    JNIEnv *env = AndroidUtil::getEnv();

    ZLLogger::Instance().registerClass("ZLXMLReader");

    bool hasEncrypt = false;
    unsigned char key[] = "hsylgwk-20120101";
//    char decryptText[] = "test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏test吴泽鹏";
//    char* outputDecry = new char[strlen(decryptText)*2+1];
//    char* outputEncry = new char[(strlen(outputDecry)-1)/2];
//    CBm53AES aes(key);
//    aes.Cipher(decryptText,outputDecry);
//    aes.InvCipherStr(outputDecry,outputEncry);
//    delete outputDecry;
//    delete outputEncry;
    bool useWindows1252 = false;
    length = stream->read(myParserBuffer, BUFFER_SIZE);
    std::string stringBuffer(myParserBuffer, length);
    stream->seek(0, true);
    int startIndex = stringBuffer.find(startTag);
    int endIndex = stringBuffer.find(endTag);
    if (startIndex >= 0 || endIndex >= 0) {
        hasEncrypt = true;
        isEncrypting = true;
//        ZLLogger::Instance().println("ZLXMLReader", "加密内容=" + stringBuffer);
    } else {
        std::string stringBuffer1(myParserBuffer, 256);
        startIndex = stringBuffer1.find('>');
        if (startIndex > 0) {
//            ZLLogger::Instance().println("ZLXMLReader", "不加密内容=" + stringBuffer1);
            stringBuffer1 = stringBuffer1.substr(0, startIndex);
            if (!ZLUnicodeUtil::isUtf8String(stringBuffer1)) {
                return false;
            }
            stringBuffer1 = ZLUnicodeUtil::toLower(stringBuffer1);
            startIndex = stringBuffer1.find("\"iso-8859-1\"");
            if (startIndex > 0) {
                useWindows1252 = true;
            }
        } else {
            ZLLogger::Instance().println("ZLXMLReader", "不识别内容=" + stringBuffer1);
        }
    }
    initialize(useWindows1252 ? charsetWindows1252 : 0);
//    myInterrupted = false;
    if (hasEncrypt) {
        bool firstIn = true;
        std::string stringBuf1;
        do {
            length = stream->read(myParserBuffer, BUFFER_SIZE);
            //属于加密内容，需要进行解密
//            std::string stringBuf1(myParserBuffer, BUFFER_SIZE);
            if (!stringBuf1.empty()) {
//                stringBuf1.erase(0, stringBuf1.length());
                stringBuf1.erase(stringBuf1.begin(),stringBuf1.end());
            }
//            stringBuf1.assign(myParserBuffer);
            stringBuf1.assign(myParserBuffer,0,length);
            if(stringBuf1 == "$</start>"){
                continue;
            }
            int startIndexTmp = stringBuf1.find(startTag);
            int endIndexTmp = stringBuf1.find(endTag);
            if (firstIn) {
//                ZLLogger::Instance().println("ZLXMLReader", "解密前=" + stringBuf1);
                if (startIndexTmp >= 0 && endIndexTmp >= 0 && (startIndexTmp + startTag.length()) < endIndexTmp) {
                    std::string beforeStr = stringBuf1.substr(0, startIndexTmp + startTag.length());
                    std::string afterStr = stringBuf1.substr(endIndexTmp + endTag.length(),
                                                             stringBuf1.length());
                    std::string encryptStr = stringBuf1.substr(startIndexTmp + startTag.length(),
                                                               endIndexTmp);
//                    ZLLogger::Instance().println("ZLXMLReader", "密文：" + encryptStr);
                    std::strcpy(myTempParserBuffer, beforeStr.c_str());
                    const char *constc = encryptStr.c_str();
                    char *encrypt = const_cast<char *>(constc);
                    std::string javaResult = callJavaMethod(env, encrypt);
//                    ZLLogger::Instance().println("ZLXMLReader", "java解密结果=" + javaResult);
                    std::strcat(myTempParserBuffer, javaResult.c_str());
//                    std::strcat(myTempParserBuffer, afterStr.c_str());
//					std::string stringBuf(myTempParserBuffer, strlen(myTempParserBuffer));
                    firstIn = true;
                } else if (startIndexTmp >= 0 && endIndexTmp < 0 && isEncrypting) {
                    isEncrypting = true;
                    std::string encryptStr = stringBuf1.substr(startIndexTmp + startTag.length(),
                                                               stringBuf1.length());
//                    ZLLogger::Instance().println("ZLXMLReader", "密文：" + encryptStr);
                    const char *constc = encryptStr.c_str();
                    char *encrypt = const_cast<char *>(constc);
                    std::string javaResult = callJavaMethod(env, encrypt);
//                    ZLLogger::Instance().println("ZLXMLReader", "java解密结果=" + javaResult);
                    std::string unEncryptStr = stringBuf1.substr(0, startIndexTmp);
                    std::strcpy(myTempParserBuffer, unEncryptStr.c_str());
                    std::strcat(myTempParserBuffer, javaResult.c_str());
//					std::string stringBuf(myTempParserBuffer, strlen(myTempParserBuffer));
                    firstIn = true;
                } else if (startIndexTmp < 0 && endIndexTmp >= 0 && isEncrypting) {
                    std::string encryptStr = stringBuf1.substr(0, endIndexTmp);
//                    ZLLogger::Instance().println("ZLXMLReader", "密文：" + encryptStr);
                    const char *constc = encryptStr.c_str();
                    char *encrypt = const_cast<char *>(constc);
                    std::string javaResult = callJavaMethod(env, encrypt);
//                    ZLLogger::Instance().println("ZLXMLReader", "java解密结果=" + javaResult);
					std::string unEncryptStr = stringBuf1.substr(endIndexTmp+endTag.length(), stringBuf1.length());
                    std::strcpy(myTempParserBuffer, javaResult.c_str());
					std::strcat(myTempParserBuffer,unEncryptStr.c_str());
//					std::string stringBuf(myTempParserBuffer, strlen(myTempParserBuffer));
                    isEncrypting = false;
                    firstIn = true;
                } else if (startIndexTmp < 0 && endIndexTmp < 0 && isEncrypting) {
//                    ZLLogger::Instance().println("ZLXMLReader", "密文：" + stringBuf1);
                    const char *constc = stringBuf1.c_str();
                    char *encrypt = const_cast<char *>(constc);
                    std::string javaResult = callJavaMethod(env, encrypt);
                    std::strcpy(myTempParserBuffer, javaResult.c_str());
//                    std::string stringBuf(myTempParserBuffer,strlen(myTempParserBuffer));
                    firstIn = true;
                }
            }
            if (!readFromBuffer(myTempParserBuffer,
                                strlen(myTempParserBuffer))) {//strlen(myParserBuffer)
                ZLLogger::Instance().println("ZLXMLReader", "解析出错了");
                break;
            }
//            if(firstIn){
//                firstIn = false;
//                if (!readFromBuffer(myTempParserBuffer, strlen(myTempParserBuffer))) {//strlen(myParserBuffer)
//                    ZLLogger::Instance().println("ZLXMLReader", "第一次解密部分停止了");
//                    break;
//                }
//            }else{
//                if (!readFromBuffer(myParserBuffer, length)) {//strlen(myParserBuffer)
//                    ZLLogger::Instance().println("ZLXMLReader", "停止了");
//                    break;
//                }
//            }
        } while ((length == BUFFER_SIZE) && !myInterrupted);
    } else {
        do {
            length = stream->read(myParserBuffer, BUFFER_SIZE);
            if (!readFromBuffer(myParserBuffer, length)) {
                break;
            }
        } while ((length == BUFFER_SIZE) && !myInterrupted);
    }
    stream->close();
//    initialize(useWindows1252 ? charsetWindows1252 : 0);
    shutdown();

    return true;
}

void ZLXMLReader::initialize(const char *encoding) {
    myInternalReader->init(encoding);
    myInterrupted = false;
    myNamespaces.push_back(new nsMap());
}

void ZLXMLReader::shutdown() {
    myNamespaces.clear();
}

bool ZLXMLReader::readFromBuffer(const char *data, std::size_t len) {
    return myInternalReader->parseBuffer(data, len);
}

bool ZLXMLReader::processNamespaces() const {
    return false;
}

const std::vector<std::string> &ZLXMLReader::externalDTDs() const {
    static const std::vector<std::string> EMPTY_VECTOR;
    return EMPTY_VECTOR;
}

void ZLXMLReader::collectExternalEntities(std::map<std::string, std::string> &entityMap) {
}

const char *ZLXMLReader::attributeValue(const char **xmlattributes, const char *name) const {
    while (*xmlattributes != 0) {
        bool useNext = std::strcmp(*xmlattributes, name) == 0;
        ++xmlattributes;
        if (*xmlattributes == 0) {
            return 0;
        }
        if (useNext) {
            return *xmlattributes;
        }
        ++xmlattributes;
    }
    return 0;
}

std::map<std::string, std::string> ZLXMLReader::attributeMap(const char **xmlattributes) const {
    std::map<std::string, std::string> map;
    while (*xmlattributes != 0) {
        std::string key = *xmlattributes;
        ++xmlattributes;
        if (*xmlattributes == 0) {
            break;
        }
        map[key] = *xmlattributes;
        ++xmlattributes;
    }
    return map;
}

ZLXMLReader::NamePredicate::~NamePredicate() {
}

ZLXMLReader::SimpleNamePredicate::SimpleNamePredicate(const std::string &name) : myName(name) {
}

bool ZLXMLReader::SimpleNamePredicate::accepts(const ZLXMLReader &, const char *name) const {
    return myName == name;
}

bool ZLXMLReader::SimpleNamePredicate::accepts(const ZLXMLReader &, const std::string &name) const {
    return myName == name;
}

ZLXMLReader::IgnoreCaseNamePredicate::IgnoreCaseNamePredicate(const std::string &lowerCaseName)
        : myLowerCaseName(lowerCaseName) {
}

bool
ZLXMLReader::IgnoreCaseNamePredicate::accepts(const ZLXMLReader &reader, const char *name) const {
    std::string lc = name;
    ZLStringUtil::asciiToLowerInline(lc);
    return myLowerCaseName == lc;
}

bool
ZLXMLReader::IgnoreCaseNamePredicate::accepts(const ZLXMLReader &, const std::string &name) const {
    std::string lc = name;
    ZLStringUtil::asciiToLowerInline(lc);
    return myLowerCaseName == lc;
}

ZLXMLReader::FullNamePredicate::FullNamePredicate(const std::string &ns, const std::string &name)
        : myNamespaceName(ns), myName(name) {
}

bool ZLXMLReader::FullNamePredicate::accepts(const ZLXMLReader &reader, const char *name) const {
    return accepts(reader, std::string(name));
}

bool
ZLXMLReader::FullNamePredicate::accepts(const ZLXMLReader &reader, const std::string &name) const {
    const std::size_t index = name.find(':');
    const std::string namespaceId =
            index == std::string::npos ? std::string() : name.substr(0, index);

    const nsMap &namespaces = reader.namespaces();
    nsMap::const_iterator it = namespaces.find(namespaceId);
    return
            it != namespaces.end() &&
            it->second == myNamespaceName &&
            name.substr(index + 1) == myName;
}

ZLXMLReader::BrokenNamePredicate::BrokenNamePredicate(const std::string &name) : myName(name) {
}

bool ZLXMLReader::BrokenNamePredicate::accepts(const ZLXMLReader &reader, const char *name) const {
    return accepts(reader, std::string(name));
}

bool ZLXMLReader::BrokenNamePredicate::accepts(const ZLXMLReader &reader,
                                               const std::string &name) const {
    return myName == name.substr(name.find(':') + 1);
}

const char *
ZLXMLReader::attributeValue(const char **xmlattributes, const NamePredicate &predicate) const {
    while (*xmlattributes != 0) {
        bool useNext = predicate.accepts(*this, *xmlattributes);
        ++xmlattributes;
        if (*xmlattributes == 0) {
            return 0;
        }
        if (useNext) {
            return *xmlattributes;
        }
        ++xmlattributes;
    }
    return 0;
}

bool
ZLXMLReader::testTag(const std::string &ns, const std::string &name, const std::string &tag) const {
    const nsMap &nspaces = namespaces();

    if (name == tag) {
        const nsMap::const_iterator it = nspaces.find(std::string());
        return it != nspaces.end() && ns == it->second;
    }
    const int nameLen = name.size();
    const int tagLen = tag.size();
    if (tagLen < nameLen + 2) {
        return false;
    }
    if (ZLStringUtil::stringEndsWith(tag, name) && tag[tagLen - nameLen - 1] == ':') {
        const nsMap::const_iterator it = nspaces.find(tag.substr(0, tagLen - nameLen - 1));
        return it != nspaces.end() && ns == it->second;
    }
    return false;
}

bool ZLXMLReader::readDocument(shared_ptr<ZLAsynchronousInputStream> stream) {
    ZLXMLReaderHandler handler(*this);
    return stream->processInput(handler);
}

const std::string &ZLXMLReader::errorMessage() const {
    return myErrorMessage;
}

void ZLXMLReader::setErrorMessage(const std::string &message) {
    myErrorMessage = message;
    interrupt();
}

std::size_t ZLXMLReader::getCurrentPosition() const {
    return myInternalReader != 0 ? myInternalReader->getCurrentPosition() : (std::size_t) -1;
}
