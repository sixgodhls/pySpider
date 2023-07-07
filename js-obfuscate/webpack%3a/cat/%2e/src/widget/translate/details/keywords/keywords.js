'use strict';
/**
 * @file 重点词汇功能模块
 * @author huangfengtao@baidu.com
 */
require('./keywords.scss');
var template = require('@/widget/common/third_party/template.js');
var env = require('@/widget/common/environment.js');
const Favo = require('@/widget/translate/favo/favo');
const pGrab = require('@/widget/translate/input/pGrab');
const domainTrans = require('@/widget/translate/domain/domain');
const details = require('../details');
const { getAcsSign } = require('@/widget/common/paris');

const $resultContainer = $('#left-result-container');

var keywords = {
    build: function (res) {
        let isFirstCard = parseInt(env.get('firstModuleId'), 10) === 1;
        var dataArray = res.data.splice(0, 10);
        var from = res.from;
        var to = res.to;
        var meanNeedHighlight = false;
        var tplData = {};
        if (from === 'zh' && to === 'en') {
            meanNeedHighlight = true;
            /* global _hmt */
            _hmt.push(['_trackEvent', '首页', 'Web中英方向重点词汇卡片展现次数']);
            tplData.direction = 'zh2en';
        } else {
            _hmt.push(['_trackEvent', '首页', 'Web英中方向重点词汇卡片展现次数']);
            tplData.direction = 'en2zh';
        }
        $.each(dataArray, function (idx, val) {
            val.href = '/#' + from + '/' + to + '/' + encodeURIComponent(val.word);
        });
        tplData.dataArray = dataArray;
        tplData.meanNeedHighlight = meanNeedHighlight;
        tplData.isFirstCard = isFirstCard;
        const html = template('tpl-keywords', tplData);
        $resultContainer.append(html);
        dataArray.forEach(({ word, means }, idx) => {
            const keywordFavo = new Favo({
                pos: 'keyword',
                target: $(`.keywords-container .keywords-content[data-key-word-idx=\'${idx}\'] .op-favo`)[0]
            });
            keywordFavo.init({
                query: word,
                from,
                to,
                fanyiDst: means.join('; ')
            });
        });
    },

    init: function () {
        this.bindEvent();
    },
    bindEvent: function () {
        var self = this;
        $('.translate-wrap').on('click', '.keywords-word', async function (e) {
            const $keywordBtn = $(this);
            const direction = $keywordBtn.attr('data-direction');
            const query = $keywordBtn.text();
            if (direction === 'zh2en') {
                _hmt.push(['_trackEvent', '首页', 'Web中英方向重点词汇二次翻译次数']);
            } else {
                _hmt.push(['_trackEvent', '首页', 'Web英中方向重点词汇二次翻译次数']);
            }
            const from = direction.split('2')[0];
            const to = direction.split('2')[1];
            const paramData = {
                from,
                to,
                query,
                transtype: 'enter',
                /* eslint-disable fecs-camelcase */
                simple_means_flag: 3,
                sign: pGrab(query),
                token: window.common.token,
                domain: domainTrans.getCurDomain()
            };
            const sign = await getAcsSign();
            window.get_cookie=await getAcsSign();
            $.ajax({
                type: 'POST',
                url: `/v2transapi?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`,
                cache: false,
                data: paramData,
                headers: {
                    'Acs-Token': sign
                }
            }).done(function (response) {
                $('.keywords-word').removeClass('selected');
                $keywordBtn.addClass('selected');
                $resultContainer.children(':not(.keywords-outter, .keywords-outter-notfirst)').remove();
                details.init(response, from, to, query, false, true);
            });
        }).on('dblclick.keywords', '.keywords-inner .highlight', function (e) {
            // highlight 区域双击事件
            self.getHighlightQueryAndSave($(this));
        }).on('mouseover.keywords', '.keywords-inner .highlight', function (e) {
            // hover 到 highlight 统计
            _hmt.push(['_trackEvent', '首页', 'Web重点词汇卡片highlight次数']);
        });
    },
    getHighlightQueryAndSave: function ($target) {
        // 双击 highlight 区域，划词翻译时翻译 highlight 区域的内容
        var highlightQuery = $target.text();
        var highlightArea = 'keywords';
        env.set('dbClickHighlightZone', {
            query: $.trim(highlightQuery),
            area: highlightArea
        });
    }
};

$(function () {
    keywords.init();
});

exports.build = function (res) {
    keywords.build(res);
};
