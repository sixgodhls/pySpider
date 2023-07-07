
// var prompt = require('./prompt.js');
// var textarea = require('./textarea.js');
// var util = require('../../common/util.js');
// var output = require('../output/output.js');
// var config = require('@/widget/common/config/trans.js');
// var processor = require('./processlang.js');
// var string = require('@/widget/common/string.js');
// var soundIcon = require('./soundicon.js');
// var hash = require('./hash.js');
// var env = require('@/widget/common/environment.js');
// var longText = require('./longtext.js');
// var simpledict = require('@/widget/translate/details/dictionary/simplemeans.js');
// var history = require('../history/history.js');
// var cookie = require('@/widget/common/cookie.js');
// const pGrab = require('./pGrab.js');
// const adLink = require('@/widget/translate/details/adLink/adLink.js');
// const domainTrans = require('../domain/domain');
// const fanyiUtil = require('@/widget/common/fanyiUtil.js').default;
// const md5 = require('@/widget/common/third_party/md5');
// const langMap = require('@/widget/common/config/langMap.js');



var translate = {
    // obj.transtype就是发给后端统计用的，前端目前没用到 2019.2.1
    onTrans: function (obj) {
        if (!simpledict.translateStopRepeat()) {
            simpledict.shutdownAudio();
        }
        // 获取输入框数据
        var $textarea = textarea.getElem();
        var query = textarea.getVal();
        var self = this;
        var transtype = obj && obj.transtype;

        // 判断数据是否为url，如果是则跳转到transpage接口做网页翻译
        if (util.isUrl(query)) {
            this.translateWebPage(query);
            return;
        }

        if (this.isQueryValid(query)) {
            var detectQuery = query;

            if (detectQuery.length > 50) {
                // 有一种情况下 encodeURIComponent 会报错，就是把高低位分开了，单独对高位 encode 会报错
                // emmm... 坑
                // https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
                detectQuery = string.cutByByte(detectQuery, 0, 150).replace(/[\uD800-\uDBFF]$/, '');
            }

            var paramData = {
                query: detectQuery
            };

            $.ajax({
                url: '/langdetect',
                type: 'POST',
                data: paramData,
                success: function (res) {
                    if (res.error === 0 && res.lan) {
                        self.langIsDeteced(res.lan, query, transtype);
                    } else {
                        showToast(`语言检测失败，请稍后重试 ${(typeof res === 'object')
                                ? JSON.stringify(res)
                                : res
                            }`);
                        self.reponseQuery(query);
                    }
                },
                error: function () {
                    showToast('语言检测失败，请稍后重试');
                }
            });
        }
    },

    /**
     * 发送给后端自动检测query语言
     *
     * @param {string} query query
     * @return {boolean}
     */
    isQueryValid: function (query) {
        if (!query) {
            return false;
        }
        return true;
    },

    // 超长情况提示处理
    processQuery: function (query) {
        var processedQuery = query;
        // 2020.12.28 PM 要解封成 5000 字符
        // if (string.getByte(query) > config.MAX_QUERY_COUNT) {
        if (query.length > config.MAX_QUERY_COUNT) {
            env.set('needLongtextTip', true);
            var tempQuery = query;
            processedQuery = query.substr(0, 5000);

            tempQuery = query.substr(0, 3);
            longText.showTip({
                query: tempQuery
            });
        }

        return processedQuery;
    },

    /**
     * 语言检测返回后操作,包含了对语言方向的处理和最后发起翻译请求
     *
     * @param {string} lang 检测到的语言
     * @param {string} query 检测的query
     * @param {string} transtype 翻译来源
     * @private
     */
    async langIsDeteced(detectLang, query, transtype, afterOcr) {
        if (detectLang === null) {
            return;
        }

        let fromLang = $('.select-from-language .language-selected').attr('data-lang');
        let toLang = $('.select-to-language .language-selected').attr('data-lang');
        let isFromLangDetected = $('.select-from-language .language-selected').attr('data-detected') === '1';

        // 可能检测到不支持的语种，这里需要手工设置成 LAN-UNKNOWN
        let isDetectedLangSupported = langMap.hasOwnProperty(detectLang);
        if (env.get('fromLangIsAuto') && !isDetectedLangSupported) {
            detectLang = 'LAN-UNKNOWN';
        }

        let badCaseByForce = false;
        if (env.get('langChangedByUser') && detectLang === toLang) {
            badCaseByForce = true;
        }

        let data = null;
        if (afterOcr && !env.get('fromLangIsAuto') && fromLang !== detectLang) {
            data = processor.processOcrLang(detectLang, fromLang, toLang);
        } else {
            prompt.show(detectLang, fromLang);
            data = processor.getLang(detectLang, fromLang, toLang);
        }
        // 没检测出来语种且用户选的源语言是自动检测就不翻译了
        if (detectLang === 'LAN-UNKNOWN' && (fromLang === 'auto' || isFromLangDetected)) {
            const clear = require('@/widget/translate/input/clear.js');
            clear.clearOutput();
            return;
        }
        // ab 策略交换完语言方向后再判断并显示发音
        soundIcon.show();
        // 语种更新后更新领域信息
        $('body').trigger('updateFromTo.domainTrans', data);
        var that = this;
        query = this.processQuery(query);
        /* eslint-disable fecs-camelcase */
        var paramData = {
            from: data.fromLang,
            to: data.toLang,
            query: query,
            transtype: transtype,
            simple_means_flag: 3,
            sign: pGrab(query),
            token: window.common.token,
            domain: domainTrans.getCurDomain()
        };
        domainTrans.log();
        /* eslint-enable fecs-camelcase */

        if (this.translateXHR && this.translateXHR.readyState !== 4) {
            this.translateXHR.abort();
        }
        const sign = await getAcsSign();
        this.translateXHR = $.ajax({
            type: 'POST',
            url: `/v2transapi?from=${encodeURIComponent(data.fromLang)}&to=${encodeURIComponent(data.toLang)}`,
            cache: false,
            data: paramData,
            headers: {
                'Acs-Token': sign
            },
            error(jqXHR, textStatus, errorThrown) {
                if (textStatus === 'abort') {
                    return;
                }
                statTransError(`${jqXHR.status} ${textStatus} ${errorThrown} ${data.fromLang} ${data.toLang} ${query}`);
            }
        }).done(function (response) {
            // 设置标志位，防止设置 hash 时重置输入框内容
            env.set('isInRtTransState', true);
            that.translateSuccess(response, data.fromLang, data.toLang, query, badCaseByForce);
        });
    },

    translateWebPage(query) {
        let baseUrl = '/';
        if (location.protocol === 'https:') {
            baseUrl = 'http://fanyi.baidu.com/';
        }
        document.location.href = [
            baseUrl + 'transpage?',
            'query=' + encodeURIComponent(query),
            '&source=url',
            '&ie=utf8',
            '&from=' + $('.select-from-language .language-selected').attr('data-lang'),
            '&to=' + $('.select-to-language .language-selected').attr('data-lang'),
            '&render=1'
        ].join('');
    },

    textareaFocus() {
        textarea.focus();
    },

    translateSuccess(response, fromLang, toLang, query, badCaseByForce) {
        if (!textarea.getVal()) {
            env.set('isInRtTransState', false);
            return;
        }
        // 只要符合翻译的长度要求，就保存到history里面
        if (query) {
            history.add(fromLang, toLang, $.trim(query));
        }
        history.hideHistory();
        adLink.hide();

        // 检查下返回结果
        if (response.error) {
            statTransError(`errno: ${response.error} ${fromLang} ${toLang} ${query}`);
        } else if (
            !response.trans_result ||
            !Array.isArray(response.trans_result.data) ||
            response.trans_result.data.length === 0
        ) {
            statTransError(`empty result ${fromLang} ${toLang} ${query} logid: ${response.logid}`);
        }

        output.checkResponse({
            res: response,
            from: fromLang,
            to: toLang,
            query: query,
            badCaseByForce: badCaseByForce
        });
        hash.setHash({
            query
        });
    },
    reponseQuery() { }
};

exports.onTranslate = function (obj) {
    translate.onTrans(obj);
};

exports.translateAfterOcr = function (lang, query) {
    /* global _hmt */
    _hmt.push(['_trackEvent', '首页', '59_首页页面_翻译query量_图片']);
    translate.langIsDeteced(lang, query, undefined, true);
};

function statTransError(information) {
    const key = 'web_error_report';
    const params = JSON.stringify({
        information
    });
    showToast(information);
    fanyiUtil
        .newFetch(
            '/track',
            {
                key,
                params,
                sign: md5(`key${key}params${params}${key}`)
            },
            'POST',
            undefined,
            true
        )
        .catch(() => { });
}

function showToast(text = '请求失败') {
    const showToastDiv = [
        '<div class="fanyi-message">',
        '<div class="fanyi-message-wrapper">',
        '<span class="fanyi-message-content"></span>',
        '<span class="close-icon">x</div>',
        '</div>',
        '</div>'
    ].join('');
    const isExist = $('.fanyi-message').length;

    if (!isExist) {
        $(document.body).append(showToastDiv);
        $('.fanyi-message .close-icon').on('click', () => {
            $('.fanyi-message').hide();
        });
    }
    $('.fanyi-message .fanyi-message-content').text(`${(new Date()).toLocaleString()
        } ${text
        } BAIDUID:${cookie.getCookie('BAIDUID')
        }`);
    $('.fanyi-message').hide().stop().fadeIn(300);
}
// The module cache
var __webpack_module_cache__ = {};

// The require function
function __webpack_require__(moduleId) {
	// Check if module is in cache
	var cachedModule = __webpack_module_cache__[moduleId];
	if (cachedModule !== undefined) {
		return cachedModule.exports;
	}
	// Create a new module (and put it into the cache)
	var module = __webpack_module_cache__[moduleId] = {
		id: moduleId,
		loaded: false,
		exports: {}
	};

	// Execute the module function
	__webpack_modules__[moduleId].call(module.exports, module, module.exports, __webpack_require__);

	// Flag the module as loaded
	module.loaded = true;

	// Return the exports of the module
	return module.exports;
}

// expose the modules object (__webpack_modules__)
// __webpack_require__.m = __webpack_modules__;

function getAcsSign() {
    var reject='狗';
    return new Promise((resolve, reject) => {
        Paris.getAcsInstance((error, instance) => {
            if (error) {
                const errorCode = error.code || 600;
                resolve(errorCode);
                return;
            }
            instance.getSign((error, sign) => {
                if (error) {
                    const errorCode = error.code || 600;
                    resolve(errorCode);
                    return;
                }
                resolve(sign);
            });
        });
    });
}
console.log(getAcsSign())