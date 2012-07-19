package org.ekfliu.gmusic.deduplicate.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ekfliu.gmusic.deduplicate.model.FindDuplicateOption;
import org.ekfliu.gmusic.deduplicate.model.HashFunction;
import org.ekfliu.gmusic.deduplicate.model.PreserveFunction;

public class FindDuplicateOptionPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JCheckBox titleCheck = new JCheckBox("title", true);
    private JCheckBox artistCheck = new JCheckBox("artist", true);
    private JCheckBox albumCheck = new JCheckBox("album", false);
    private JCheckBox composerCheck = new JCheckBox("composer", false);
    private JCheckBox yearCheck = new JCheckBox("year", false);
    private JCheckBox genreCheck = new JCheckBox("genre", false);
    private JCheckBox albumArtistCheck = new JCheckBox("albumArtist", false);
    private JCheckBox nameCheck = new JCheckBox("name", false);
    private JCheckBox lengthCheck = new JCheckBox("length", false);
    private JComboBox hashOption = new JComboBox(new Object[] {
                                                     HashFunction.MD5,
                                                     HashFunction.SHA
                                                 });
    private JComboBox preservOption = new JComboBox(new Object[] {
                                                        PreserveFunction.Newest,
                                                        PreserveFunction.Oldest
                                                    });

    public FindDuplicateOptionPanel() {
        super();
        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // setLayout(new GridLayout(4, 3));
        setLayout(new BorderLayout());

        final JPanel checkPanel = new JPanel();
        checkPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Hashed Properties"));
        checkPanel.setLayout(new GridLayout(3, 3));
        checkPanel.add(titleCheck);
        checkPanel.add(artistCheck);
        checkPanel.add(albumCheck);
        checkPanel.add(composerCheck);
        checkPanel.add(yearCheck);
        checkPanel.add(genreCheck);
        checkPanel.add(albumArtistCheck);
        checkPanel.add(nameCheck);
        checkPanel.add(lengthCheck);
        add(checkPanel, BorderLayout.WEST);

        final JPanel optionPanel = new JPanel();
        optionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Options"));
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.add(getLabeledPanel("Hash Function", hashOption));
        optionPanel.add(getLabeledPanel("Preserve Order", preservOption));
        add(optionPanel, BorderLayout.EAST);
    }

    protected JPanel getLabeledPanel(final String aLabel, final JComponent aComponent) {
        final JPanel labelPanel = new JPanel();
        labelPanel.add(new JLabel(aLabel));
        labelPanel.add(aComponent);

        return labelPanel;
    }
    public FindDuplicateOption getFindOptions() {
        final FindDuplicateOption option = new FindDuplicateOption();
        option.setAlbum(albumCheck.isSelected());
        option.setAlbumArtist(albumArtistCheck.isSelected());
        option.setArtist(artistCheck.isSelected());
        option.setComposer(composerCheck.isSelected());
        option.setGenre(genreCheck.isSelected());
        option.setName(nameCheck.isSelected());
        option.setTitle(titleCheck.isSelected());
        option.setYear(yearCheck.isSelected());
        option.setLength(lengthCheck.isSelected());
        option.setHashFunction((HashFunction) hashOption.getSelectedItem());
        option.setPreservFunction((PreserveFunction) preservOption.getSelectedItem());

        return option;
    }
}
