package it.polimi.dice.verification.ui.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;

import com.google.gson.Gson;

import it.polimi.dice.verification.DiceVerificationPlugin;
import it.polimi.dice.verification.json.StormTopology;
import it.polimi.dice.verification.launcher.VerificationLaunchConfigurationAttributes;
import it.polimi.dice.verification.launcher.VerificationLaunchConfigurationDelegate;
import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;
import it.polimi.dice.verification.uml.diagrams.classdiagram.BoltClass;
import it.polimi.dice.verification.uml.diagrams.classdiagram.SpoutClass;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;
import it.polimi.dice.vtconfig.VerificationToolConfig;
import it.polimi.dice.vtconfig.VtConfigFactory;
import it.polimi.dice.vtconfig.ZotPlugin;
import it.polimi.dice.vtconfig.util.VerificationToolConfigSerializer;
import it.polimi.dice.core.logger.DiceLogger;
import it.polimi.dice.core.ui.dialogs.ContainerSelectionDialog;
import it.polimi.dice.core.ui.dialogs.FileSelectionDialog;

public class MainLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	private static final Image CHECKED = DiceVerificationUiPlugin.getDefault().getImageRegistry().get(DiceVerificationUiPlugin.IMG_CHECKED);;
	private static final Image UNCHECKED = DiceVerificationUiPlugin.getDefault().getImageRegistry().get(DiceVerificationUiPlugin.IMG_UNCHECKED);

	private class MapEntryViewerFloatComparator extends ViewerComparator {
		private static final int DESCENDING = 1;
		private int propertyIndex = 0;
		private int direction = -DESCENDING; // ASCENDING

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int column) {
			if (column == this.propertyIndex) {
				// Same column as last sort; toggle the direction
				direction = 1 - direction;
			} else {
				// New column; do an ascending sort
				this.propertyIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			@SuppressWarnings("unchecked")
			Entry<String, Float> entry1 = (Entry<String, Float>) e1;
			@SuppressWarnings("unchecked")
			Entry<String, Float> entry2 = (Entry<String, Float>) e2;
			int result = 0;
			switch (propertyIndex) {
			case 0:
				result = entry1.getKey().compareTo(entry2.getKey());
				break;
			case 1:
				result = entry1.getValue().compareTo(entry2.getValue());
				break;
			default:
				result = 0;
			}
			// If descending order, flip the direction
			if (direction == DESCENDING) {
				result = -result;
			}
			return result;
		}
	}

	private class MapEntryViewerBooleanComparator extends ViewerComparator {
		private static final int DESCENDING = 1;
		private int propertyIndex = 0;
		private int direction = -DESCENDING; // ASCENDING

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int column) {
			if (column == this.propertyIndex) {
				// Same column as last sort; toggle the direction
				direction = 1 - direction;
			} else {
				// New column; do an ascending sort
				this.propertyIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			@SuppressWarnings("unchecked")
			Entry<String, Boolean> entry1 = (Entry<String, Boolean>) e1;
			@SuppressWarnings("unchecked")
			Entry<String, Boolean> entry2 = (Entry<String, Boolean>) e2;
			int result = 0;
			switch (propertyIndex) {
			case 0:
				result = entry1.getKey().compareTo(entry2.getKey());
				break;
			case 1:
				result = entry1.getValue().compareTo(entry2.getValue());
				break;
			default:
				result = 0;
			}
			// If descending order, flip the direction
			if (direction == DESCENDING) {
				result = -result;
			}
			return result;
		}
	}

	
	
	private class FormData {
		private String inputFile;
		private String intermediateFilesDir;
		private int timeBound;
		private boolean keepIntermediateFiles;
		private VerificationToolConfig config;
		
		{
			setConfig(VtConfigFactory.eINSTANCE.createVerificationToolConfig());
		}
		
		protected String getInputFile() {
			return inputFile;
		}
		protected void setInputFileBoolean(String inputFile) {
			this.inputFile = inputFile;
			String readableInputFile = toReadableString(inputFile);
			inputFileText.setText(readableInputFile != null ? readableInputFile : StringUtils.EMPTY);
			config.getMonitoredBolts().clear();
			Set<String> vars = getVariablesFromUmlModelDummy(new File(URI.create(inputFile)));
			for (String var : vars) {
				config.getMonitoredBolts().put(var, false);
			}
			for (Map.Entry<String, Boolean> entry : config.getMonitoredBolts().entrySet()) {
				DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), "Key: "+ entry.getKey() + " - Value: " + entry.getValue());
			}
			viewerBoolean.refresh();
			setDirty(true);
			updateLaunchConfigurationDialog();
		}
		protected void setInputFileFloat(String inputFile) {
			this.inputFile = inputFile;
			String readableInputFile = toReadableString(inputFile);
			inputFileText.setText(readableInputFile != null ? readableInputFile : StringUtils.EMPTY);
			config.getVariableAssignments().clear();
			Set<String> vars = getVariablesFromUmlModelDummy(new File(URI.create(inputFile)));
			for (String var : vars) {
				config.getVariableAssignments().put(var, 1.0f);
			}
			for (Map.Entry<String, Float> entry : config.getVariableAssignments().entrySet()) {
				DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), "Key: "+ entry.getKey() + " - Value: " + entry.getValue());
			}
			viewerFloat.refresh();
			setDirty(true);
			updateLaunchConfigurationDialog();
		}

		protected String getIntermediateFilesDir() {
			return intermediateFilesDir;
		}
		
		protected void setIntermediateFilesDir(String intermediateFilesDir) {
			this.intermediateFilesDir = intermediateFilesDir;
			String readableFilesDir = toReadableString(intermediateFilesDir);
			intermediateFilesDirText.setText(readableFilesDir != null ? readableFilesDir : StringUtils.EMPTY);
			setDirty(true);
			updateLaunchConfigurationDialog();
		}

		
		protected int getTimeBound(){
			return timeBound;
		}
		
		protected void setTimeBound(int timeBound) {
			this.timeBound = timeBound;
//			String timeBoundString = String.valueOf(timeBound);
//			timeBoundText.setText(timeBoundString != null ? timeBoundString : StringUtils.EMPTY);
			timeBoundSpinner.setSelection(timeBound);
			setDirty(true);
			updateLaunchConfigurationDialog();
		}
		protected boolean keepIntermediateFiles() {
			return keepIntermediateFiles;
		}
		protected void setKeepIntermediateFiles(boolean keepIntermediateFiles) {
			this.keepIntermediateFiles = keepIntermediateFiles;
			keepIntermediateFilesButton.setSelection(keepIntermediateFiles);
			intermediateFilesDirText.setEnabled(keepIntermediateFiles());
			browseIntermediateFilesDirButton.setEnabled(keepIntermediateFiles);
			setDirty(true);
			updateLaunchConfigurationDialog();
		}
		public VerificationToolConfig getConfig() {
			return config;
		}
		
		public void setConfig(VerificationToolConfig config) {
			this.config = config;
			config.eAdapters().add(new EContentAdapter() {
				@Override
				public void notifyChanged(Notification notification) {
					super.notifyChanged(notification);
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});
			if (viewerBoolean != null) {
				viewerBoolean.setInput(data);
			}
			if (viewerFloat != null) {
				viewerFloat.setInput(data);
			}

			setDirty(true);
			updateLaunchConfigurationDialog();
		}
		
		private String toReadableString(String fileUriString) {
			URI uri = URI.create(fileUriString);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			if (new File(uri).isFile()) {
				IFile[] files = root.findFilesForLocationURI(uri);
				if (files.length > 0) {
					return org.eclipse.emf.common.util.URI.createPlatformResourceURI(
							files[0].getFullPath().toString(), false).toString();
				}
			} else {
				IContainer[] containers = root.findContainersForLocationURI(uri);
				if (containers.length > 0) {
					return org.eclipse.emf.common.util.URI.createPlatformResourceURI(
							containers[0].getFullPath().toString(), false).toString();
				}
			}
			return null;
		}
	}
	

	private class DataContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			return ((FormData)inputElement).getConfig().getVariableAssignments().toArray();
		}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		@Override
		public void dispose() {
		}
	}
	
	private class DataContentProviderBoolean implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			return ((FormData)inputElement).getConfig().getMonitoredBolts().toArray();
		}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		@Override
		public void dispose() {
		}
	}
	
	private class ValueEditingSupportFloat extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		public ValueEditingSupportFloat(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			this.editor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getValue(Object element) {
			return ((Entry<String, Float>) element).getValue().toString();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void setValue(Object element, Object userInputValue) {
			try {
				Float value = Float.valueOf(userInputValue.toString());
				((Entry<String, Float>) element).setValue(value);
				viewer.update(element, null);
			} catch (Throwable t) {
				ErrorDialog.openError(getShell(), Messages.MainLaunchConfigurationTab_errorTitle, Messages.MainLaunchConfigurationTab_invalidFloatError, new Status(IStatus.ERROR, DiceVerificationPlugin.PLUGIN_ID, t.getLocalizedMessage(), t));
			}
		}
	}

	private class ValueEditingSupportBoolean extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		public ValueEditingSupportBoolean(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			//this.editor = new TextCellEditor(viewer.getTable());
			this.editor = new CheckboxCellEditor(viewer.getTable(), SWT.CHECK);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
			//return new CheckboxCellEditor(null, SWT.CHECK);
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getValue(Object element) {
			return ((Entry<String, Boolean>) element).getValue();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void setValue(Object element, Object userInputValue) {
			try {
				Boolean value = (Boolean)userInputValue;   //Boolean.valueOf(userInputValue.toString());
				((Entry<String, Boolean>) element).setValue(value);
				viewer.update(element, null);
			} catch (Throwable t) {
				ErrorDialog.openError(getShell(), Messages.MainLaunchConfigurationTab_errorTitle, Messages.MainLaunchConfigurationTab_invalidBooleanError, new Status(IStatus.ERROR, DiceVerificationPlugin.PLUGIN_ID, t.getLocalizedMessage(), t));
			}
		}
	}

	
	
	protected Text inputFileText;
	protected Button keepIntermediateFilesButton;
	protected Text timeBoundText;
	protected Spinner timeBoundSpinner;
	protected Text intermediateFilesDirText;
	protected Button browseIntermediateFilesDirButton;
	protected TableViewer viewerBoolean, viewerFloat;
	protected TableViewerColumn varViewerColumnFloat, varViewerColumnBoolean;
	protected TableViewerColumn valueViewerColumnFloat, valueViewerColumnBoolean;
	
	protected FormData data = new FormData();
	
	@Override
	public void createControl(Composite parent) {
		Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout(1, true));

		GridData buttonsGridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		buttonsGridData.widthHint = 100;
		
		{ // Model Group
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			
			group.setLayout(new GridLayout(2,  false));
			group.setText(Messages.MainLaunchConfigurationTab_modelLabel);
			
			inputFileText = new Text(group, SWT.BORDER);
			inputFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			inputFileText.setEditable(false);
			
			Button fileButton = new Button(group, SWT.NONE);
			fileButton.setText(Messages.MainLaunchConfigurationTab_browsLabel);
			fileButton.setLayoutData(buttonsGridData);
			
			fileButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileSelectionDialog dialog = new FileSelectionDialog(getShell());
					dialog.setValidator(dialog.new IsFileStatusValidator() {
						@Override
						public IStatus validate(Object[] selection) {
							IStatus superResult = super.validate(selection);
							if (!superResult.isOK()) {
								return superResult;
							} else {
								IFile file = (IFile) selection[0];
								if (isUmlModel(new File(file.getLocationURI()))) {
									return new Status(IStatus.OK, DiceVerificationUiPlugin.PLUGIN_ID, StringUtils.EMPTY);
								} else {
									return new Status(IStatus.ERROR, DiceVerificationUiPlugin.PLUGIN_ID, Messages.MainLaunchConfigurationTab_invalidUmlFileError);
								}
							}
						}
					});
					if (data.getInputFile() != null) {
						IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(URI.create(data.getInputFile()));
						dialog.setInitialSelection(files);
					}
					if (dialog.open() == Dialog.OK) {
						//data.setInputFileFloat(dialog.getFile().getLocationURI().toString());
						data.setInputFileBoolean(dialog.getFile().getLocationURI().toString());
					}
				}
			});
		}
		
		{ // Options Group
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			
			group.setLayout(new GridLayout(2,  false));
			group.setText(Messages.MainLaunchConfigurationTab_intermediateFilesLabel);

			keepIntermediateFilesButton = new Button(group, SWT.CHECK);
			keepIntermediateFilesButton.setText(Messages.MainLaunchConfigurationTab_saveIntermediateLabel);
			keepIntermediateFilesButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			
			intermediateFilesDirText = new Text(group, SWT.BORDER);
			intermediateFilesDirText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			intermediateFilesDirText.setEnabled(false);
			intermediateFilesDirText.setEditable(false);
			
			browseIntermediateFilesDirButton = new Button(group, SWT.NONE);
			browseIntermediateFilesDirButton.setText(Messages.MainLaunchConfigurationTab_browseLabel);
			browseIntermediateFilesDirButton.setLayoutData(buttonsGridData);
			browseIntermediateFilesDirButton.setEnabled(false);

			keepIntermediateFilesButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button button = (Button)e.widget;
					data.setKeepIntermediateFiles(button.getSelection());
				}
			});

			browseIntermediateFilesDirButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell());
					if (dialog.open() == Dialog.OK) {
						data.setIntermediateFilesDir(dialog.getContainer().getLocationURI().toString());
					}
				}
			});
			
		}
		
		//Time Bound group
/*		{
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			
			group.setLayout(new GridLayout(2,  false));
			group.setText(Messages.MainLaunchConfigurationTab_timeBoundLabel);
			
			timeBoundText = new Text(group, SWT.BORDER);
			timeBoundText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			timeBoundText.setEnabled(true);
			timeBoundText.setEditable(true);
			timeBoundText.addVerifyListener(new VerifyListener() {

		        @Override
		        public void verifyText(VerifyEvent e) {

		            Text text = (Text)e.getSource();

		            // get old text and create new text by using the VerifyEvent.text
		            final String oldS = text.getText();
		            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

		            boolean isFloat = true;
		            try
		            {
		                Float.parseFloat(newS);
		            }
		            catch(NumberFormatException ex)
		            {
		                isFloat = false;
		            }

		            System.out.println(newS);

		            if(!isFloat)
		                e.doit = false;
		        }
		    });
			
		}
*/		
		{
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			
			group.setLayout(new GridLayout(2,  false));
			group.setText(Messages.MainLaunchConfigurationTab_timeBoundLabel);
			timeBoundSpinner = new Spinner(group, SWT.BORDER);
			timeBoundSpinner.setMaximum(100);
			timeBoundSpinner.setMinimum(10);
			//timeBoundSpinner.setIncrement(1);
			
		}
			
		
		{ // Zot Plugin group
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			
			group.setLayout(new RowLayout(SWT.VERTICAL));
			group.setText(Messages.MainLaunchConfigurationTab_zotPluginLabel);
			
			Button ae2sbvzotButton = new Button(group, SWT.RADIO);
			ae2sbvzotButton.setText(Messages.MainLaunchConfigurationTab_ae2sbvzotLabel);
			ae2sbvzotButton.setSelection(true);
			data.getConfig().setZotPlugin(ZotPlugin.AE2SBVZOT);
			ae2sbvzotButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					data.getConfig().setZotPlugin(ZotPlugin.AE2SBVZOT);
				};
			});
			
			Button ae2bvzot = new Button(group, SWT.RADIO);
			ae2bvzot.setText(Messages.MainLaunchConfigurationTab_ae2bvzotLabel);
			ae2bvzot.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					data.getConfig().setZotPlugin(ZotPlugin.AE2BVZOT);
				};
			});
			
			Button ae2zot = new Button(group, SWT.RADIO);
			ae2zot.setText(Messages.MainLaunchConfigurationTab_ae2zotLabel);
			ae2zot.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					data.getConfig().setZotPlugin(ZotPlugin.AE2ZOT);
				};
			});

		}
		
		{ // Configuration Group - Set Monitored Bolts
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			group.setLayout(new GridLayout(1,  false));
			group.setText(Messages.MainLaunchConfigurationTab_monitoredBoltsLabel);
			
			Composite tableComposite = new Composite(group, SWT.NONE);
			tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			viewerBoolean = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
			viewerBoolean.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			viewerBoolean.getTable().setLinesVisible(true);
			viewerBoolean.getTable().setHeaderVisible(true);

			
			viewerBoolean.setContentProvider(new DataContentProviderBoolean());
			
			
			viewerBoolean.setInput(data);
			
			MapEntryViewerBooleanComparator comparator = new MapEntryViewerBooleanComparator();
			viewerBoolean.setComparator(comparator);

			varViewerColumnBoolean = new TableViewerColumn(viewerBoolean, SWT.NONE);
		    varViewerColumnBoolean.getColumn().setText(Messages.MainLaunchConfigurationTab_boltLabel);
		    varViewerColumnBoolean.getColumn().setResizable(true);
			varViewerColumnBoolean.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					@SuppressWarnings("unchecked")
					Entry<String, Boolean> entry = (Entry<String, Boolean>) element;
					DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), "Key: "+ entry.getKey() + " - Value: " + entry.getValue());
					return entry.getKey();
				}
			});
			varViewerColumnBoolean.getColumn().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MapEntryViewerBooleanComparator comparator = (MapEntryViewerBooleanComparator) viewerBoolean.getComparator();
					comparator.setColumn(0);
			        int dir = comparator.getDirection();
			        viewerBoolean.getTable().setSortDirection(dir);
			        viewerBoolean.getTable().setSortColumn(varViewerColumnBoolean.getColumn());
			        viewerBoolean.refresh();
				}
			});

			
			valueViewerColumnBoolean = new TableViewerColumn(viewerBoolean, SWT.NONE);
			valueViewerColumnBoolean.getColumn().setText(Messages.MainLaunchConfigurationTab_monitoredLabel);
			valueViewerColumnBoolean.getColumn().setResizable(true);
			valueViewerColumnBoolean.setLabelProvider(new ColumnLabelProvider() {
				
				@Override
			      public String getText(Object element) {
			    	 // return null;
			    	  Entry<String, Boolean> entry = (Entry<String, Boolean>) element;
			    	  if (entry.getValue()) {
				          return "Check";
				        } else {
				          return "Uncheck";
				        }
			      }

			      @SuppressWarnings("unchecked")
				@Override
			      public Image getImage(Object element) {
			    	Entry<String, Boolean> entry = (Entry<String, Boolean>) element;
					
			        if (entry.getValue()) {
			          return CHECKED;
			          
			        } else {
			          return UNCHECKED;
			        }
			      }
			    });
			

			valueViewerColumnBoolean.setEditingSupport(new ValueEditingSupportBoolean(viewerBoolean));
			valueViewerColumnBoolean.getColumn().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MapEntryViewerBooleanComparator comparator = (MapEntryViewerBooleanComparator) viewerBoolean.getComparator();
					comparator.setColumn(1);
			        int dir = comparator.getDirection();
			        viewerBoolean.getTable().setSortDirection(dir);
			        viewerBoolean.getTable().setSortColumn(valueViewerColumnBoolean.getColumn());
			        viewerBoolean.refresh();
				}
			});
			TableColumnLayout tableLayout = new TableColumnLayout();
			tableLayout.setColumnData(varViewerColumnBoolean.getColumn(), new ColumnWeightData(1));
			tableLayout.setColumnData(valueViewerColumnBoolean.getColumn(), new ColumnWeightData(2));
			tableComposite.setLayout(tableLayout);
			viewerBoolean.getTable().setSortColumn(varViewerColumnBoolean.getColumn());
			viewerBoolean.getTable().setSortDirection(SWT.UP);
		}
		
		
/*		{ // Configuration Group 2 - float
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			group.setLayout(new GridLayout(1,  false));
			group.setText(Messages.MainLaunchConfigurationTab_variablesLabel);
			
			Composite tableComposite = new Composite(group, SWT.NONE);
			tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			viewerFloat = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
			viewerFloat.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			viewerFloat.getTable().setLinesVisible(true);
			viewerFloat.getTable().setHeaderVisible(true);

			viewerFloat.setContentProvider(new DataContentProvider());
			
			viewerFloat.setInput(data);
			
			MapEntryViewerFloatComparator comparator = new MapEntryViewerFloatComparator();
			viewerFloat.setComparator(comparator);

			varViewerColumnFloat = new TableViewerColumn(viewerFloat, SWT.NONE);
		    varViewerColumnFloat.getColumn().setText(Messages.MainLaunchConfigurationTab_variableLabel);
		    varViewerColumnFloat.getColumn().setResizable(true);
			varViewerColumnFloat.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					@SuppressWarnings("unchecked")
					Entry<String, Float> entry = (Entry<String, Float>) element;
					return entry.getKey();
				}
			});
			varViewerColumnFloat.getColumn().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MapEntryViewerFloatComparator comparator = (MapEntryViewerFloatComparator) viewerFloat.getComparator();
					comparator.setColumn(0);
			        int dir = comparator.getDirection();
			        viewerFloat.getTable().setSortDirection(dir);
			        viewerFloat.getTable().setSortColumn(varViewerColumnFloat.getColumn());
			        viewerFloat.refresh();
				}
			});

			
			valueViewerColumnFloat = new TableViewerColumn(viewerFloat, SWT.NONE);
			valueViewerColumnFloat.getColumn().setText(Messages.MainLaunchConfigurationTab_valueLabel);
			valueViewerColumnFloat.getColumn().setResizable(true);
			valueViewerColumnFloat.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					@SuppressWarnings("unchecked")
					Entry<String, Float> entry = (Entry<String, Float>) element;
					return entry.getValue().toString();
				}
			});
			valueViewerColumnFloat.setEditingSupport(new ValueEditingSupportFloat(viewerFloat));
			valueViewerColumnFloat.getColumn().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MapEntryViewerFloatComparator comparator = (MapEntryViewerFloatComparator) viewerFloat.getComparator();
					comparator.setColumn(1);
			        int dir = comparator.getDirection();
			        viewerFloat.getTable().setSortDirection(dir);
			        viewerFloat.getTable().setSortColumn(valueViewerColumnFloat.getColumn());
			        viewerFloat.refresh();
				}
			});
			TableColumnLayout tableLayout = new TableColumnLayout();
			tableLayout.setColumnData(varViewerColumnFloat.getColumn(), new ColumnWeightData(1));
			tableLayout.setColumnData(valueViewerColumnFloat.getColumn(), new ColumnWeightData(3));
			tableComposite.setLayout(tableLayout);
			viewerFloat.getTable().setSortColumn(varViewerColumnFloat.getColumn());
			viewerFloat.getTable().setSortDirection(SWT.UP);
		}
*/
		
		setControl(topComposite);
	}

	
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.removeAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false);
		configuration.removeAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR);
		configuration.removeAttribute(VerificationLaunchConfigurationAttributes.VERIFICATION_CONFIGURATION);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 15);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE)) {
				//data.setInputFileFloat(configuration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY));
				data.setInputFileBoolean(configuration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY));
			}
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES)) {
				data.setKeepIntermediateFiles(configuration.getAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false));
			}
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR)) {
				data.setIntermediateFilesDir(configuration.getAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR, StringUtils.EMPTY));
			}
			
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND)) {
				data.setTimeBound(configuration.getAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 20));
			}
			String serializedConfig = StringUtils.EMPTY;

			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.VERIFICATION_CONFIGURATION)) {
				try {
					serializedConfig = configuration.getAttribute(VerificationLaunchConfigurationAttributes.VERIFICATION_CONFIGURATION, StringUtils.EMPTY);
					data.setConfig(VerificationToolConfigSerializer.deserialize(serializedConfig));
//					DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), "Ciao");
				} catch (IOException e) {
					DiceLogger.logException(DiceVerificationUiPlugin.getDefault(),
							MessageFormat.format(Messages.MainLaunchConfigurationTab_unableParserError, serializedConfig), e);
				}
			}
		} catch (CoreException e) {
			DiceLogger.logException(DiceVerificationUiPlugin.getDefault(), e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, data.getInputFile());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, data.keepIntermediateFiles());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR, data.intermediateFilesDir);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.VERIFICATION_CONFIGURATION, VerificationToolConfigSerializer.serialize(data.getConfig()));
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, timeBoundSpinner.getSelection());
	}

	@Override
	public String getName() {
		return Messages.MainLaunchConfigurationTab_mainTabTitle;
	}

	@Override
	public Image getImage() {
		return DiceVerificationUiPlugin.getDefault().getImageRegistry().get(DiceVerificationUiPlugin.IMG_VERIF_MAIN_TAB);
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration configuration) {
		try {
			// Catch no input file defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE)) {
				setErrorMessage(Messages.MainLaunchConfigurationTab_noInpuntError);
				return false;
			}
			// Catch no keep intermediate files flag defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES)) {
				return false;
			}
			// Catch no configuration defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.VERIFICATION_CONFIGURATION)) {
				return false;
			}
			// Check input file exists
			File inputFile = new File(URI.create(configuration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY)));
			if (!inputFile.isFile()) {
				// Should not happen...
				setErrorMessage(Messages.MainLaunchConfigurationTab_inputNotExistsError);
				return false;
			}
			if (!isUmlModel(inputFile)) {
				// Should not happen
				setErrorMessage(Messages.MainLaunchConfigurationTab_notUml2InputError);
				return false;
			}
			// Check input file exists
			if (configuration.getAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false)) {
				// If keep intermediate files, catch no directory defined
				if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR)) {
					setErrorMessage(Messages.MainLaunchConfigurationTab_noDirForIntermediateError);
					return false;
				}
				// Check directory exists
				File intermediateFilesDir = new File(URI.create(configuration.getAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR, StringUtils.EMPTY)));
				if (!intermediateFilesDir.isDirectory()) {
					// Should not happen...
					setErrorMessage(Messages.MainLaunchConfigurationTab_intermediateDirNotExistError);
					return false;
				}			
			}
		} catch (CoreException e) {
			DiceLogger.logException(DiceVerificationUiPlugin.getDefault(), e);
		}
		setErrorMessage(null);
		return true;
	}
	
	protected boolean isUmlModel(File file) {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = null;
		try { 
			resource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(file.getAbsolutePath()), true);
			EObject eObject = resource.getContents().get(0);
			if (UMLPackage.eINSTANCE.getNsURI().equals(eObject.eClass().getEPackage().getNsURI())) {
				return true;
			} else {
				return false;
			}
		} catch (Throwable t) {
			// Unable to get the first root element
			// The file is not a valid EMF resource
		}
		return false;
	}
	
	
	protected Set<String> getVariablesFromUmlModelDummy(File file) {
		Set<String> vars = new HashSet<>();
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = null;
		StormTopology topology = new StormTopology();
		List<SpoutClass> spouts = new ArrayList<>();
		List<BoltClass> bolts = new ArrayList<>();
		Gson gson = new Gson();
		//XMLResource r2 = null;
		try { 
			resource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(file.getAbsolutePath()), true);
		//	r2 = (XMLResource)resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(file.getAbsolutePath()), true);
			for (Iterator<EObject> it = resource.getAllContents(); it.hasNext();) {
				EObject eObject = it.next();
				if(eObject instanceof org.eclipse.uml2.uml.Class){
					Element element = (Element)eObject;
					if (UML2ModelHelper.isSpout(element)) {
						SpoutClass sc = new SpoutClass((org.eclipse.uml2.uml.Class)eObject);
						spouts.add(sc);
					}
					else if (UML2ModelHelper.isBolt(element)) {
						BoltClass bc = new BoltClass((org.eclipse.uml2.uml.Class)eObject);
						vars.add(bc.getId());
						bolts.add(bc);
						DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), gson.toJson(bc));
				}
				topology.setBolts(bolts);
				topology.setSpouts(spouts);
				DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), gson.toJson(topology));
				}
			}
			
		} catch (Throwable t) {
			DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), t);
		}
		return vars;
	}
  

	protected Set<String> getVariablesFromUmlModel(File file) {
		Set<String> vars = new HashSet<>();
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = null;
		try { 
			resource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(file.getAbsolutePath()), true);
			for (Iterator<EObject> it = resource.getAllContents(); it.hasNext();) {
				EObject eObject = it.next();
				if (eObject instanceof Element) {
					Element element = (Element) eObject;
					for (Stereotype stereotype : element.getAppliedStereotypes()) {
						for (Property property : stereotype.getAllAttributes()) {
							if (property.getType() instanceof DataType) {
								DataType dataType = (DataType) property.getType();
								boolean isMarteType = isDefinedInMarteLibrary(dataType);
								if (!isMarteType) {
								    for (Classifier classifier : dataType.getGenerals()) {
								        if (isDefinedInMarteLibrary(classifier)) {
								        	isMarteType = true;
								        	break;
								        }
								    }
							    }
								if (isMarteType) {
						            List<String> collectedValues = new ArrayList<>();
						            if (!property.isMultivalued()) {
						            	Object value = element.getValue(stereotype, property.getName());
						            	if (value instanceof String) {
						            		collectedValues.add((String) value);
						            	}
						            } else {
						            	for (Object value : (List<?>) element.getValue(stereotype, property.getName())) {
						            		if (value instanceof String) {
							            		collectedValues.add((String) value);
							            	}	
						            	}
						            }
						            for (String string : collectedValues) {
						            	Pattern pattern = Pattern.compile("[^\\W]\\w+=(\\$\\w+)[$\\W]"); //$NON-NLS-1$
						            	Matcher matcher = pattern.matcher(string);
					            		while (matcher.find()) {
					            			vars.add(matcher.group(1));
					            		}
						            }
								}
							}
						}
					}
				}
			}
			
		} catch (Throwable t) {
			DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), t);
		}
		return vars;
	}

	
	protected Set<String> getVariablesFromUmlModelOld(File file) {
		Set<String> vars = new HashSet<>();
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = null;
		try { 
			resource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(file.getAbsolutePath()), true);
			for (Iterator<EObject> it = resource.getAllContents(); it.hasNext();) {
				EObject eObject = it.next();
				if (eObject instanceof Element) {
					Element element = (Element) eObject;
					for (Stereotype stereotype : element.getAppliedStereotypes()) {
						for (Property property : stereotype.getAllAttributes()) {
							if (property.getType() instanceof DataType) {
								DataType dataType = (DataType) property.getType();
								boolean isMarteType = isDefinedInMarteLibrary(dataType);
								if (!isMarteType) {
								    for (Classifier classifier : dataType.getGenerals()) {
								        if (isDefinedInMarteLibrary(classifier)) {
								        	isMarteType = true;
								        	break;
								        }
								    }
							    }
								if (isMarteType) {
						            List<String> collectedValues = new ArrayList<>();
						            if (!property.isMultivalued()) {
						            	Object value = element.getValue(stereotype, property.getName());
						            	if (value instanceof String) {
						            		collectedValues.add((String) value);
						            	}
						            } else {
						            	for (Object value : (List<?>) element.getValue(stereotype, property.getName())) {
						            		if (value instanceof String) {
							            		collectedValues.add((String) value);
							            	}	
						            	}
						            }
						            for (String string : collectedValues) {
						            	Pattern pattern = Pattern.compile("[^\\W]\\w+=(\\$\\w+)[$\\W]"); //$NON-NLS-1$
						            	Matcher matcher = pattern.matcher(string);
					            		while (matcher.find()) {
					            			vars.add(matcher.group(1));
					            		}
						            }
								}
							}
						}
					}
				}
			}
			
		} catch (Throwable t) {
			DiceLogger.logError(DiceVerificationUiPlugin.getDefault(), t);
		}
		return vars;
	}
	
	private boolean isDefinedInMarteLibrary(Classifier classifier) {
	    Package pkg = classifier.getNearestPackage();
	    while (pkg != null) {
	        if ("MARTE_Library".equals(pkg.getQualifiedName())) { //$NON-NLS-1$
	            return true;
	        }
	        pkg = pkg.getNestingPackage();
	    }
	    return false;
	}
}