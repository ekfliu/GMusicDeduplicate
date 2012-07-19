package org.ekfliu.gmusic.deduplicate;

import gmusic.api.comm.ApacheConnector;

import gmusic.api.impl.GoogleMusicAPI;

import gmusic.api.interfaces.IGoogleMusicAPI;

import gmusic.api.model.Song;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.ekfliu.gmusic.deduplicate.model.DuplicateFileTreeModel;
import org.ekfliu.gmusic.deduplicate.model.FindDuplicateOption;
import org.ekfliu.gmusic.deduplicate.ui.FindDuplicateOptionPanel;
import org.ekfliu.gmusic.deduplicate.ui.treetable.CustomTableHeaderRenderer;
import org.ekfliu.gmusic.deduplicate.ui.treetable.JTreeTable;
import org.ekfliu.gmusic.deduplicate.ui.treetable.ResizeableTableHeader;

public class DeduplicateSongs {
    protected static JFrame mainFrame;
    protected static JTreeTable mainTreeTable;
    protected static JTextField usernameField;
    protected static JPasswordField passwordField;
    protected static FindDuplicateOptionPanel duplicatePanel;
    protected static List<String> duplicateSongIds = new ArrayList<String>();
    protected static DuplicateFileTreeModel treeModel;

    protected static JPanel constructInputPanel(final JFrame aMainFrame) {
        final JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        inputPanel.setLayout(new GridBagLayout());

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 4;
        c.ipady = 3;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("username"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        inputPanel.add(usernameField, c);
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("password"), c);
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        inputPanel.add(passwordField, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        duplicatePanel = new FindDuplicateOptionPanel();
        inputPanel.add(duplicatePanel, c);

        return inputPanel;
    }
    protected static JPanel constructButtonPanel(final JFrame aMainFrame) {
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        final Action autoSizeAction =
            new AbstractAction("Auto Table Size") {
                /**
                *
                */
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent aE) {
                    ((ResizeableTableHeader) mainTreeTable.getTableHeader()).resizeAllColumns();
                }
            };

        buttonPanel.add(new JButton(autoSizeAction));

        final Action findAction =
            new AbstractAction("Find Dups") {
                /**
                *
                */
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent aE) {
                    final String username = usernameField.getText().trim();
                    final String password = new String(passwordField.getPassword());
                    final ProgressMonitor monitor = new ProgressMonitor(mainFrame, "Checking for Duplicate...", "", 0, 100);
                    monitor.setMillisToPopup(0);
                    monitor.setProgress(0);

                    final SwingWorker<?, ?> worker = new FindDuplicateWorker(monitor, username, password, duplicatePanel.getFindOptions());
                    worker.execute();
                }
            };

        buttonPanel.add(new JButton(findAction));

        final Action deleteAction =
            new AbstractAction("Delete Dups") {
                /**
                *
                */
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent aE) {
                    final String username = usernameField.getText().trim();
                    final String password = new String(passwordField.getPassword());
                    final ProgressMonitor monitor = new ProgressMonitor(mainFrame, "Deleting Duplicate...", "", 0, 100);
                    monitor.setMillisToPopup(0);
                    monitor.setProgress(0);

                    final SwingWorker<?, ?> worker = new DeleteDuplicateWorker(monitor, username, password, duplicateSongIds);
                    worker.execute();
                }
            };

        buttonPanel.add(new JButton(deleteAction));

        final Action exitAction =
            new AbstractAction("Exit") {
                /**
                *
                */
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent aE) {
                    System.exit(0);
                }
            };

        buttonPanel.add(new JButton(exitAction));

        return buttonPanel;
    }
    public static void main(final String[] aArgs) throws Exception {
        mainFrame = new JFrame("GMusic Deduplicator");
        mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent aE) {
                    System.exit(0);
                }
                @Override
                public void windowClosing(final WindowEvent aE) {
                    System.exit(0);
                }
            });

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(constructInputPanel(mainFrame), BorderLayout.NORTH);
        mainPanel.add(constructButtonPanel(mainFrame), BorderLayout.SOUTH);
        treeModel = new DuplicateFileTreeModel();
        mainTreeTable = new JTreeTable(treeModel);

        final ResizeableTableHeader tableHeader = new ResizeableTableHeader(mainTreeTable.getColumnModel());
        tableHeader.setAutoResizingEnabled(false);
        tableHeader.setIncludeHeaderWidth(true);
        tableHeader.setResizeVisibleOnly(true);
        tableHeader.setReorderingAllowed(false);

        final ImageIcon sortUpIcon = new ImageIcon(DeduplicateSongs.class.getResource("/sortuparrow.gif"));
        final ImageIcon sortDownIcon = new ImageIcon(DeduplicateSongs.class.getResource("/sortdownarrow.gif"));
        tableHeader.setDefaultRenderer(new CustomTableHeaderRenderer(treeModel, sortUpIcon, sortDownIcon));
        tableHeader.addPropertyChangeListener(treeModel);
        mainTreeTable.setTableHeader(tableHeader);
        mainTreeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final JScrollPane scrollPane = new JScrollPane(mainTreeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    protected static class DeleteDuplicateWorker extends SwingWorker<Void, Integer> {
        private ProgressMonitor monitor;
        private String username;
        private String password;
        private List<String> deleteIdList;

        public DeleteDuplicateWorker(final ProgressMonitor aMonitor, final String aUsername, final String aPassword, final List<String> aDeleteIdList) {
            super();
            monitor = aMonitor;
            username = aUsername;
            password = aPassword;
            deleteIdList = aDeleteIdList;
        }

        @Override
        protected Void doInBackground() throws Exception {
            final IGoogleMusicAPI api = new GoogleMusicAPI(new ApacheConnector(), new File("."));
            api.login(username, password);
            api.deleteSongs(deleteIdList.toArray(new String[deleteIdList.size()]));

            return null;
        }
        @Override
        protected void process(final List<Integer> aChunks) {
            for (Integer progress : aChunks) {
                System.out.print(" " + progress + " ");
                monitor.setProgress(progress);
            }
        }
        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException ee) {
            } catch (InterruptedException ie) {
            } finally {
                monitor.close();
            }
        }
    }

    protected static class FindDuplicateWorker extends SwingWorker<Map<String, List<Song>>, Integer> {
        private ProgressMonitor monitor;
        private String username;
        private String password;
        private FindDuplicateOption options;

        public FindDuplicateWorker(final ProgressMonitor aMonitor, final String aUsername, final String aPassword, final FindDuplicateOption aOptions) {
            super();
            monitor = aMonitor;
            username = aUsername;
            password = aPassword;
            options = aOptions;
        }

        @Override
        protected Map<String, List<Song>> doInBackground()
                                                  throws Exception {
            final IGoogleMusicAPI api = new GoogleMusicAPI(new ApacheConnector(), new File("."));
            api.login(username, password);

            final Collection<Song> songCollect = api.getAllSongs();
            publish(20);

            final int totalSize = songCollect.size();
            System.out.println(String.format("%s songs detected for user %s", totalSize, username));

            final Map<String, List<Song>> duplicateMap = new HashMap<String, List<Song>>();
            final HashCalculator hashCalc;

            switch (options.getHashFunction()) {
                case MD5:
                    hashCalc = new MD5HashCalculator();

                    break;

                case SHA:
                    hashCalc = new SHAHashCalculator();

                    break;

                default:
                    hashCalc = new SHAHashCalculator();

                    break;
            }

            int counter = 0;

            for (final Song current : songCollect) {
                final String hashCode = calculateMD5ForSong(current, options, hashCalc);

                if (!duplicateMap.containsKey(hashCode)) {
                    duplicateMap.put(hashCode, new ArrayList<Song>());
                }

                duplicateMap.get(hashCode).add(current);
                counter++;
                publish(20 + (int) Math.round(40 * ((double) counter / totalSize)));
            }

            System.out.println(String.format("%s unique songs detected for user %s", duplicateMap.size(), username));

            int notDuplicateCount = 0;
            final Iterator<Entry<String, List<Song>>> iter = duplicateMap.entrySet().iterator();
            final PreserveCalculator preserveCalculator = new CreationDatePreserveCalculator(options.getPreservFunction());
            counter = 0;

            while (iter.hasNext()) {
                final Entry<String, List<Song>> entry = iter.next();

                if (entry.getValue().size() > 1) {
                    final List<Song> duplicateList = entry.getValue();
                    preserveCalculator.processDuplicateList(duplicateList);
                } else {
                    iter.remove();
                    notDuplicateCount++;
                }

                counter++;
                publish(60 + (int) Math.round(40 * ((double) counter / totalSize)));
            }

            System.out.println(String.format("%s not duplicated songs, %s duplicated songs detected for user %s", notDuplicateCount, duplicateMap.size(),
                                             username));

            return duplicateMap;
        }
        @Override
        protected void process(final List<Integer> aChunks) {
            for (Integer progress : aChunks) {
                System.out.print(" " + progress + " ");
                monitor.setProgress(progress);
            }
        }
        @Override
        protected void done() {
            try {
                final Map<String, List<Song>> duplicateMap = get();
                duplicateSongIds.clear();

                for (List<Song> duplicateList : duplicateMap.values()) {
                    for (int i = 0; i < (duplicateList.size() - 1); i++) {
                        final Song delete = duplicateList.get(i);
                        duplicateSongIds.add(delete.getId());
                    }
                }

                treeModel.updateNewDuplicateMap(duplicateMap);
            } catch (ExecutionException ee) {
            } catch (InterruptedException ie) {
            } finally {
                monitor.close();
            }
        }
    }

    protected static String calculateMD5ForSong(final Song aSong, final FindDuplicateOption aOption, final HashCalculator aCalculator)
                                         throws Exception {
        aCalculator.resetInit();

        if (aOption.isAlbum()) {
            aCalculator.updateHash(String.valueOf(aSong.getAlbum()));
        }

        if (aOption.isAlbumArtist()) {
            aCalculator.updateHash(String.valueOf(aSong.getAlbumArtist()));
        }

        if (aOption.isArtist()) {
            aCalculator.updateHash(String.valueOf(aSong.getArtist()));
        }

        if (aOption.isComposer()) {
            aCalculator.updateHash(String.valueOf(aSong.getComposer()));
        }

        if (aOption.isGenre()) {
            aCalculator.updateHash(String.valueOf(aSong.getGenre()));
        }

        if (aOption.isName()) {
            aCalculator.updateHash(String.valueOf(aSong.getName()));
        }

        if (aOption.isTitle()) {
            aCalculator.updateHash(String.valueOf(aSong.getTitle()));
        }

        if (aOption.isYear()) {
            aCalculator.updateHash(String.valueOf(aSong.getYear()));
        }

        if (aOption.isLength()) {
            aCalculator.updateHash(String.valueOf(aSong.getDurationMillis()));
        }

        return aCalculator.finalizeHash();
    }
}
