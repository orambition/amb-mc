package amb.sponge.plugin.constant;


import org.spongepowered.api.text.Text;

public class PluginText {
    public static final Text bookInfo = Text.of("恭喜你！已经可以使用传送书了！" +
            "制作方法：使用笔将一本书命名为 传送书 或 tpbook 即可制作一本传送书；" +
            "使用方法：手持传送书，点击鼠标右键，即可打开传送页面；" +
            "注意：" +
            "1.每制作一本传送书会消耗30点经验" +
            "2.每人可以免费设置1个传送点；" +
            "3.超过1个时，设置新<传送点>需要消耗<绿宝石x传送点个数^2>和20点经验；" +
            "3.传送需要消耗<绿宝石x2>" +
            "4.当背包中没有绿宝石时，传送将消耗20点经验；" +
            "5.当背包中没有绿宝石且经验不足20点时，传送将消耗<传送书x1>");
}
