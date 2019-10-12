/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.LuceneTestCase;

/**
 * A very simple demo used in the API documentation (src/java/overview.html).
 *
 * Please try to keep src/java/overview.html up-to-date when making changes
 * to this class.
 */
public class TestDemo2 extends LuceneTestCase {

  public void testDemo() throws IOException {
    String text = "《骆驼祥子》的主题思想即半殖民地、半封建的中国社会底层劳苦大众的悲苦命运是共同的。\n" +
        "旧中国的军阀势力，为了抢夺利益而引发战乱，人民生活困苦，处于社会底层的祥子等劳动人民的生活更加艰辛。黑暗腐败的社会现实是造成祥子悲惨命运的根本。 [2] \n" +
        "《骆驼祥子》通过人力车夫“祥子”一生几起几落、最终沉沦的故事，揭露了半殖民地半封建的中国社会底层人民的悲苦命运。祥子的遭遇，证明了半殖民地、半封建的时代里的劳动人民想通过自己的勤劳和个人奋斗来改变处境，是根本不可能的。\n" +
        "小说刻画了许多像祥子一样的小人物形象。那些小人物中有的因战乱导致家人离散而不得不相依为命，有的不堪家庭重负，有的为养活兄弟而出卖肉体。社会底层的劳苦大众的悲剧是整整一个时代的悲剧，身处其中的每一分子到头来都逃脱不了祥子一样的命运，除非他们认清楚自己的现状，联合起来推翻那吃人的社会与制度。\n" +
        "祥子的一生，反映了20世纪20年代中国破产农民在“市民化”过程中的沉沦，因而祥子的悲剧不仅仅是他个人的悲剧，而是包含着更为深刻的文化和时代因素。作者带着对民族、文化的出路的关切来剖析祥子的命运，既从传统文明中的积极因素出发批判现代畸形文明的负面效应，为传统美德的沦落而痛惜，又不满于祥子身上所积淀的民族文化的劣根性，既诅咒那个“把人变成鬼”的黑暗的社会和制度，又痛心于无知、愚昧的善良民众在病态的旧社会的堕落。 [5] \n" +
        "作品创作\n" +
        "一、突出祥子的好人性格\n" +
        "《骆驼祥子》文本中，初进城的祥子几乎是完美的，是个绝对好人，他善良、勤劳、坚毅，并且又有着强健的身体和明确的生存目标。正如作品所写：这个小伙子“头不很大，圆眼，肉鼻子，两条眉很短、很粗，头上永远剃得发亮，腮上没有多余的肉，脖子可是几乎和头一边儿粗；脸上永远红扑扑的”，“他确乎有点像一棵树，坚壮，沉默，而又有生气”。\n" +
        "祥子不仅有充满青春活力的健壮外表，而且有一个劳动者、一个淳朴农民的善良本性：“他不怕吃苦，也没有一般洋车夫的可以原谅而不便效法的恶习”；他知足、不争，拉车从不与别人争生意，有时候甚至不肯要价，只说声“坐上吧，瞧着给”；他对生活的要求十分简单，他不吸烟、不喝酒、不近女色，甚至连包好茶叶也不喝，为的是每月能多攒下俩钱。同时他也有自己的追求，那就是拥有一辆属于自己的洋车，然后做个独立的劳动者。他知廉耻、明善恶，在第一次丢车后，他为再买车拼命抢活，落得一片骂声时，心里很惭愧。在曹先生家拉包月不小心摔了曹先生后，他是那么内疚并主动辞职承担自己的责任，希望自己能够补偿曹先生的损失。\n" +
        "二、突出社会现实的不合理\n" +
        "祥子是个绝对的好人，同时他的生活愿望又是那么普通，但是这样的祥子在当时的社会里却无法生存。为此，作品设计的四个关键性情节决定了祥子的悲剧，它们分别是被军阀抢劫、被孙侦探敲诈、被虎妞强行占有以及小福子的死亡。作品设计的其他情节，比如夏太太勾引祥子，反映了当时社会的丑恶腐烂。这些都是社会外部环境的因素，是社会现实，把祥子逼到了绝路，使他堕落。\n" +
        "老舍先生谈到《骆驼祥子》的创作时强调：他所要观察的不仅是车夫的一点点的浮现在衣冠上的、表现在言语与姿态上的那些小事情，而是要由车夫的内心状态观察到地狱究竟是什么样子。车夫外表上的一切，都必有生活与生命上的根据。老舍试图找到其根源，通过写出劳苦社会好人没好报的故事，质问“好人”所生存的社会环境。\n" +
        "老舍说：“人把自己从野兽中提拔出，可是到现在，人还把自己的同类驱到野兽里去。祥子还在那文化之城，可是变成了走兽，一点不是他自己的过错。”老舍在小说中感慨万分地说出“一个拉车的，要立在人间的最低处，等着一切人一切法一切困苦的击打”，道出了祥子的毁灭与整个旧社会有着莫大的关系。纵观祥子生活的社会现实，既有反动统治下政治的黑暗、时局的动荡；又有战乱、天灾以及资本主义经济的挤压，致使下层劳动者生活在罪恶的地狱里。祥子的形象，是在当时那个黑暗社会层面上、在他与各种社会力量的复杂关系中凸显出来的，他的悲剧是社会的产物。作为一个农村破产的失地农民，想要实现在城市拥有一辆自己的洋车做一个自食其力的独立劳动者的梦想，在当时暗无天日的旧社会简直比登天还难。黑暗的社会环境终究击垮了祥子美丽的梦想。祥子的悲剧就产生于他生活在地狱般非人的环境里，他一次又一次地同命运搏斗，所有的幻想和努力都化为泡影，恶劣的社会毁灭了一个人的全部。\n" +
        "《骆驼祥子》对社会的批判，是以人为本的社会批判。老舍站在人道主义的立场上，对被侮辱与被损害的弱者寄寓深切的关怀和同情，对于摧残人的社会进行无情的否定。\n" +
        "作者开篇写道：“祥子，不是骆驼，因为‘骆驼’只是个外号。”“骆驼——在口内负重惯了——是走不快的，不但是得慢走，还须极小心的慢走，骆驼怕滑；一汪儿水，一片儿泥，都可以教它们劈了腿，或折扭了膝。骆驼的价值全在四条腿上；腿一完，全完！”老舍把祥子比喻成骆驼，对骆驼的描写不是纯客观化的，而是带有较强的感情色彩。\n" +
        "骆驼的性情、骆驼的脆弱折射了祥子的命运。祥子为卑微的生活苦苦挣扎，他是那么不起眼，“不但吃的苦、喝的苦，连一阵风、一场雨，也给他的神经以无情的苦刑”。尽管他身上沾染了各种各样的“可以原谅而不便效法的恶习”，有时甚至是可恨可憎的，但所有的那些，并不是与生俱来的，而是生活的风雨刻印出的深深印记。\n" +
        "祥子与虎妞之间的婚姻是没有爱情的婚姻，他们之间的冲突，带有人性、文化的复杂冲突。作家老舍没有深入地开掘，而是以平庸的“平民思想”价值尺度去进行单纯的道德判断，把虎妞看作是非道德的黑暗社会的一部分，是造成祥子悲剧的重要原因之一，因此，虎妞也就成了被老舍丑化的一个市民女性形象，在作品中老舍对虎妞进行了身体和心灵的双重丑化。虎妞相貌丑陋，是个38岁的老姑娘，而且雄性化。虎头虎脑，黑铁塔似的，长着虎牙，这是老舍对虎妞身体的丑化。道德上丑化则是：流氓成性、引诱祥子、威胁逼迫祥子结婚、不是处女等，特别是用枕头装成怀孕，更是极端化的笔法。在作者笔下，虎妞完全是泼妇流氓。\n" +
        "在祥子与虎妞的婚姻生活之中作者完全把虎妞写成了一个好吃懒做的妇女。而正是由于这种好吃懒做，才导致虎妞的难产而死。特别是，老舍把祥子与虎妞结婚以后的正常的性生活，看作是肮脏的、不洁的，是对祥子的肉体摧残，譬如，新婚第二天，祥子很早就起来，然后去了澡堂子，要把女人的肮脏洗掉。作者还一再强调性对祥子的肉体摧残，认为性生活对劳动者身体具有破坏性，譬如，结婚以后，祥子拉车就没有力气，弓腰驼背等。最后，虎妞死了，祥子什么也没有剩下。老舍在这里告诉读者的是，女人是祸水，只能给人带来灾难和不幸。后来的夏太太，勾引了祥子，祥子面对夏太太的时候，只觉得她和虎妞是一路货。作品中的女人，都是男人的灾星。\n" +
        "虎妞固然有缺点，她沾染了市井社会的流氓气；但是，她又有勇敢、可爱，精明强干的一面。虎妞首先是职业女性，是一个女强人。虎妞虽然在家里，但不是一般的家庭妇女，而是企业管理者，是城市的白领阶层，带有女强人的性格特点。在工作上，她具有管理才能和经营意识，她有能力管理车厂。她父亲主外，她主内，把车厂管理得有条不紊。在和祥子结婚以后，虎妞也并没有丧失职业女性的思想性格，譬如，她要买几台车，租出去，吃车租，而不喜欢祥子拉车卖苦力，可以说她是靠管理挣钱。\n" +
        "虎妞对祥子的爱情，是追求自己的幸福生活，具有一定的个性解放的意义，或者可以说，是市井社会中自发的个性解放精神。虎妞对祥子的爱情虽然手段上卑劣，但是感情上却是真诚的、是义无返顾的。虎妞告诉祥子，怎样去先认刘四为干爹，然后一步一步地实现他们的爱情，这些都体现了她的真诚。她宁可和父亲决裂，也要死心踏地地和祥子生活在一起，这种决心从来没有动摇过。结婚以后，她全心全意地关心、照顾祥子。\n" +
        "在家庭生活中，虎妞未必就是一个坏妻子。虎妞有追求世俗的幸福生活的渴望。自己给自己操办结婚大事，租房子、装修房子，都是虎妞一个人张罗。同时，虎妞也有妇女的勤劳，她也并不是好吃懒做的，她总是把饭菜都作好，等着祥子回来吃。过节就张罗着煮元宵、包饺子、逛庙会、灯会等。新婚她要祥子好好休息，要祥子陪她上街。这一切，都是市民社会的生活追求。但是，在老舍看来，这些却是贪图安逸的腐败生活。\n" +
        "祥子与虎妞的冲突，更多的是农民与市民的冲突。祥子虽然生活在城市里，但是他的心态和价值观念完全是农民的心态和价值观念。“一方面祥子完全失去了与农村的联系，而且再也不愿与之发生任何联系了，另一方面他又没有扎根于城市生活，甚至还不知道与新的社会环境建立应有的联系”譬如祥子既拒绝了刘四主动借钱给他买车的建议，也拒绝高妈给他的放贷、储蓄和起会的建议，坚持靠自己的劳动来实现自己的理想。从这些地方虽然可以看出祥子的质朴，但同时也可以看出体现在他身上的那种农民式的根深蒂固的自给自足的经济观念，以及他与城市生活的隔膜，对商品经济的无知。祥子是一个“在新的环境里还能保留着旧的习惯”的人，而由“陌生人所组成的现代社会是无法用乡土社会的习俗来应付的”。\n" +
        "“如果祥子真的把钱存进银行或放了债，那么孙侦探的讹诈也不会对祥子造成致命的打击；如果祥子真的听从虎妞的安排坐吃车份子，那么祥子的身体也不会因饱受烈日、暴雨的打击而衰竭”。\n" +
        "在祥子与虎妞的冲突中，其实有着更为深刻的文化、人性的含义。但是，老舍却没有深入挖掘，而是以那种素朴的膨胀的平民意识，对这种冲突进行了简单的道德评判。老舍当时完全站在农民、底层市民的立场上，对于虎妞缺乏应有的理解和认同。这样，虎妞作为中层市民就成了“恶”的化身，而祥子作为底层贫民成为了“善”的化身。在善与恶的两极对立中，老舍把自己的爱都给了祥子，而把自己的恨、憎恶都给了虎妞。这样虎妞就被老舍漫画式地丑化了，她成为黑暗社会的一部分，成为祥子悲剧的最重要的社会原因之一。\n" +
        "作品中祥子形象意义，就在于体现老舍对普通人的悲悯、关怀，对于不合理的社会批判，体现了老舍的人性关怀";

    Path indexPath = Files.createTempDirectory("tempIndex");
    try (Directory dir = FSDirectory.open(indexPath)) {
      Analyzer analyzer = new StandardAnalyzer();
      try {
        IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig(analyzer));
        for (int i = 0; i < 1; i++) {

          Document doc = new Document();
          doc.add(newTextField("fieldname", text, Field.Store.YES));
          iw.addDocument(doc);
        }
        iw.commit();  // 不提交，reader 无法看见索引的数据
      }catch(Exception e){

      }

    }

    IOUtils.rm(indexPath);
  }
}
