package com.peak885.luyten;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import com.peak885.luyten.image.SvgIconLoader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -5691181006363313993L;

    private final Icon pack;
    private final Icon javaImage;
    private final Icon ymlImage;
    private final Icon fileImage;

    public CellRenderer() {
        this.pack = SvgIconLoader.load("/icons/package.svg", 16);
        this.javaImage = SvgIconLoader.load("/icons/java.svg", 16);
        this.ymlImage = SvgIconLoader.load("/icons/yml.svg", 16);
        this.fileImage = SvgIconLoader.load("/icons/file.svg", 16);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
                                                  int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getChildCount() > 0) {
            setIcon(this.pack);
        } else {
            switch (FileUtil.getLanguage(getFileName(node))) {
            case SyntaxConstants.SYNTAX_STYLE_JAVA:
                setIcon(this.javaImage);
                break;
            case SyntaxConstants.SYNTAX_STYLE_YAML:
                setIcon(this.ymlImage);
                break;
            default:
                setIcon(this.fileImage);
                break;
            }
        }

        return this;
    }

    public String getFileName(DefaultMutableTreeNode node) {
        return ((TreeNodeUserObject) node.getUserObject()).getOriginalName();
    }

}
