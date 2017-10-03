package it.polimi.dice.verification.ui.launcher.spark;

import java.io.File;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.uml2.uml.UMLPackage;

import it.polimi.dice.core.logger.DiceLogger;
import it.polimi.dice.core.ui.dialogs.ContainerSelectionDialog;
import it.polimi.dice.core.ui.dialogs.FileSelectionDialog;
import it.polimi.dice.verification.launcher.SparkAnalysisType;
import it.polimi.dice.verification.launcher.VerificationLaunchConfigurationAttributes;
import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;
import it.polimi.dice.verification.ui.launcher.common.Messages;
import it.polimi.dice.verification.ui.preferences.PreferenceConstants;
import it.polimi.dice.verification.uml.helpers.DiceProfileConstants;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;

public class SparkMainLaunchConfigurationTab extends AbstractLaunchConfigurationTab implements DirtableTab{

	protected Text taskNameText;
	protected Text taskDescriptionText;
	protected Text inputFileText;
	protected Button keepIntermediateFilesButton;
	protected Spinner timeBoundSpinner;
	protected Text intermediateFilesDirText;
	protected Button browseIntermediateFilesDirButton;
	protected Button feasibilityButton;
	protected Button boundednessButton;
	protected Spinner deadlineSpinner;
	protected Text hostText;
	protected Text portText;
	
	protected SparkVerificationFormData data = new SparkVerificationFormData(this);

	public void setTaskNameText(String taskName) {
		this.taskNameText.setText(taskName);
	}

	public void setTaskDescriptionText(String taskDescription) {
		this.taskDescriptionText.setText(taskDescription);
	}

	public void setInputFileText(String inputFile) {
		this.inputFileText.setText(inputFile);
	}

	public void setKeepIntermediateFilesButton(boolean keepIntermediateFiles) {
		this.keepIntermediateFilesButton.setSelection(keepIntermediateFiles);
		this.intermediateFilesDirText.setEnabled(keepIntermediateFiles);
		this.browseIntermediateFilesDirButton.setEnabled(keepIntermediateFiles);
	}

	public void setTimeBoundSpinner(int timeBound) {
		this.timeBoundSpinner.setSelection(timeBound);
	}

	public void setIntermediateFilesDirText(String intermediateFilesDir) {
		this.intermediateFilesDirText.setText(intermediateFilesDir);
	}

	public void setAnalysisTypeButtons(SparkAnalysisType analysisType) {
			switch (analysisType) {
			case FEASIBILITY:
				feasibilityButton.setSelection(true);
				break;

			case BOUNDEDNESS:
				boundednessButton.setSelection(true);
				break;

			default:
				feasibilityButton.setSelection(true);
				break;
			}
	}

	public void setDeadlineSpinner(int deadline) {
		this.deadlineSpinner.setSelection(deadline);
	}

	public void setHostText(String hostAddress) {
		this.hostText.setText(hostAddress);
	}

	public void setPortText(String portNumber) {
		this.portText.setText(portNumber);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout(1, true));

		GridData buttonsGridDataInput = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		buttonsGridDataInput.widthHint = 100;

		GridData buttonsGridDataIntermediate = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		buttonsGridDataIntermediate.widthHint = 100;

		{ // Task Name Group
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

			group.setLayout(new GridLayout(4, false));
			group.setText("Task Details");

			Label taskNameLabel = new Label(group, SWT.BORDER);
			taskNameLabel.setText("Task Name");
			taskNameLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

			taskNameText = new Text(group, SWT.BORDER);
			taskNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			taskNameText.setEditable(true);

			taskNameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					data.setTaskName(text.getText(), false);
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});

			Label taskDescriptionLabel = new Label(group, SWT.BORDER);
			taskDescriptionLabel.setText("Task Description");
			taskDescriptionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

			taskDescriptionText = new Text(group, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gridData.heightHint = 2 * taskDescriptionText.getLineHeight();
			taskDescriptionText.setLayoutData(gridData);
			taskDescriptionText.setEditable(true);
			taskDescriptionText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					data.setTaskDescription(text.getText(), false);
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});

		}

		{ // Model Group
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

			group.setLayout(new GridLayout(2, false));
			group.setText(Messages.MainLaunchConfigurationTab_modelLabel);

			inputFileText = new Text(group, SWT.BORDER);
			inputFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			inputFileText.setEditable(false);

			Button fileButton = new Button(group, SWT.NONE);
			fileButton.setText(Messages.MainLaunchConfigurationTab_browsLabel);
			fileButton.setLayoutData(buttonsGridDataInput);

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
									return new Status(IStatus.OK, DiceVerificationUiPlugin.PLUGIN_ID,
											StringUtils.EMPTY);
								} else {
									return new Status(IStatus.ERROR, DiceVerificationUiPlugin.PLUGIN_ID,
											Messages.MainLaunchConfigurationTab_invalidUmlFileError);
								}
							}
						}
					});
					if (data.getInputFile() != null) {
						IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
								.findFilesForLocationURI(URI.create(data.getInputFile()));
						dialog.setInitialSelection(files);
					}
					if (dialog.open() == Dialog.OK) {
						// data.setInputFileFloat(dialog.getFile().getLocationURI().toString());
						data.setInputFile(dialog.getFile().getLocationURI().toString());
					}
				}
			});
		}

		{ // Options Group
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

			group.setLayout(new GridLayout(2, false));
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
			browseIntermediateFilesDirButton.setLayoutData(buttonsGridDataIntermediate);
			browseIntermediateFilesDirButton.setEnabled(false);

			keepIntermediateFilesButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
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

		{ // Analysis Type Group
			Group grpAnalysisSettings = new Group(topComposite, SWT.NONE);
			GridLayout gl_grpAnalysisSettings = new GridLayout(2, false);
			gl_grpAnalysisSettings.marginLeft = 10;
			grpAnalysisSettings.setLayout(gl_grpAnalysisSettings);
			grpAnalysisSettings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			grpAnalysisSettings.setText(Messages.SparkMainLaunchConfigurationTab_grpAnalysisSettings_text);

			Label lblAnalysisType = new Label(grpAnalysisSettings, SWT.NONE);
			lblAnalysisType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblAnalysisType.setText(Messages.SparkMainLaunchConfigurationTab_lblAnalysisType_text);

			Label lblDeadline = new Label(grpAnalysisSettings, SWT.NONE);
			lblDeadline.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblDeadline.setText(Messages.SparkMainLaunchConfigurationTab_lblDeadline_text);

			feasibilityButton = new Button(grpAnalysisSettings, SWT.RADIO);
			feasibilityButton.setText("Feasibility");

			feasibilityButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					data.setAnalysisType(SparkAnalysisType.FEASIBILITY, false);
					setDirty(true);
					updateLaunchConfigurationDialog();
				};
			});

			deadlineSpinner = new Spinner(grpAnalysisSettings, SWT.BORDER);
			deadlineSpinner.setMaximum(999999999);
			deadlineSpinner.setMinimum(1);
			deadlineSpinner.setIncrement(1);
			deadlineSpinner.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					data.setDeadline(deadlineSpinner.getSelection());
					setDirty(true);
					updateLaunchConfigurationDialog();
				};
			});

			boundednessButton = new Button(grpAnalysisSettings, SWT.RADIO);
			boundednessButton.setText("Boundedness");
			// boundednessButton.setEnabled(false);
			boundednessButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					data.setAnalysisType(SparkAnalysisType.BOUNDEDNESS, false);
					setDirty(true);
					updateLaunchConfigurationDialog();
				};
			});

			new Label(grpAnalysisSettings, SWT.NONE);

			setAnalysisTypeButtons(data.getAnalysisType());

		}

		new Label(topComposite, SWT.NONE);

		// Time Bound group
		{
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

			group.setLayout(new GridLayout(2, false));
			group.setText(Messages.MainLaunchConfigurationTab_timeBoundLabel);
			timeBoundSpinner = new Spinner(group, SWT.BORDER);
			timeBoundSpinner.setMaximum(100);
			timeBoundSpinner.setMinimum(10);
			new Label(group, SWT.NONE);
			// timeBoundSpinner.setIncrement(1);
			timeBoundSpinner.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					data.setTimeBound(timeBoundSpinner.getSelection());
					setDirty(true);
					updateLaunchConfigurationDialog();
				};
			});

		}

		{ // Connection group
			Group group = new Group(topComposite, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

			group.setLayout(new GridLayout(4, false));
			group.setText(Messages.MainLaunchConfigurationTab_connectionLabel);

			Label hostLabel = new Label(group, SWT.BORDER);
			hostLabel.setText(Messages.MainLaunchConfigurationTab_hostAddressLabel);
			hostLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			hostText = new Text(group, SWT.BORDER);
			hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			hostText.setEditable(true);
			hostText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					data.setHostAddress(text.getText(), false);
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});

			Label portLabel = new Label(group, SWT.BORDER);
			portLabel.setText(Messages.MainLaunchConfigurationTab_portNumberLabel);
			portLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			portText = new Text(group, SWT.BORDER);
			portText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			portText.setEditable(true);
			portText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// Get the widget whose text was modified
					Text text = (Text) event.widget;
					data.setPortNumber(text.getText(), false);
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});

		}

		setControl(topComposite);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TASK_NAME, StringUtils.EMPTY);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TASK_DESCRIPTION, StringUtils.EMPTY);
		configuration.removeAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false);
		configuration.removeAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.SPARK_ANALYSIS_TYPE,
				SparkAnalysisType.FEASIBILITY.toString());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.SPARK_DEADLINE, 100);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 15);
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.HOST_ADDRESS, DiceVerificationUiPlugin
				.getDefault().getPreferenceStore().getString(PreferenceConstants.HOST.getName()));
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.PORT_NUMBER, DiceVerificationUiPlugin
				.getDefault().getPreferenceStore().getString(PreferenceConstants.PORT.getName()));
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.TASK_NAME)) {
				data.setTaskName(configuration.getAttribute(VerificationLaunchConfigurationAttributes.TASK_NAME,
						StringUtils.EMPTY), true);
			}
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.TASK_DESCRIPTION)) {
				data.setTaskDescription(configuration.getAttribute(
						VerificationLaunchConfigurationAttributes.TASK_DESCRIPTION, StringUtils.EMPTY), true);
			}
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE)) {
				data.setInputFile(configuration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE,
						StringUtils.EMPTY));
			}
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES)) {
				data.setKeepIntermediateFiles(configuration
						.getAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false));
			}
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR)) {
				data.setIntermediateFilesDir(configuration.getAttribute(
						VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR, StringUtils.EMPTY));
			}

			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND)) {
				data.setTimeBound(configuration.getAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 0));
			}

			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.SPARK_ANALYSIS_TYPE)) {
				data.setAnalysisType(SparkAnalysisType.valueOf(
						configuration.getAttribute(VerificationLaunchConfigurationAttributes.SPARK_ANALYSIS_TYPE,
								SparkAnalysisType.FEASIBILITY.toString())),
						true);
			}

			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.SPARK_DEADLINE)) {
				data.setDeadline(
						configuration.getAttribute(VerificationLaunchConfigurationAttributes.SPARK_DEADLINE, 0));
			}

			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.HOST_ADDRESS)) {
				data.setHostAddress(configuration.getAttribute(VerificationLaunchConfigurationAttributes.HOST_ADDRESS,
						"http://localhost"), true);
			}
			if (configuration.hasAttribute(VerificationLaunchConfigurationAttributes.PORT_NUMBER)) {
				data.setPortNumber(
						configuration.getAttribute(VerificationLaunchConfigurationAttributes.PORT_NUMBER, "5000"),
						true);
			}

		} catch (CoreException e) {
			DiceLogger.logException(DiceVerificationUiPlugin.getDefault(), e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TASK_NAME, data.getTaskName());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TASK_DESCRIPTION,
				data.getTaskDescription());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, data.getInputFile());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES,
				data.keepIntermediateFiles());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR,
				data.getIntermediateFilesDir());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.SPARK_ANALYSIS_TYPE,
				data.getAnalysisType().toString());
		
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.SPARK_DEADLINE, data.getDeadline());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, data.getTimeBound());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.HOST_ADDRESS, data.getHostAddress());
		configuration.setAttribute(VerificationLaunchConfigurationAttributes.PORT_NUMBER, data.getPortNumber());
	}

	@Override
	public String getName() {
		return Messages.MainLaunchConfigurationTab_mainTabTitle;
	}

	@Override
	public Image getImage() {
		return DiceVerificationUiPlugin.getDefault().getImageRegistry()
				.get(DiceVerificationUiPlugin.IMG_VERIF_MAIN_TAB_SPARK);
	}

	@Override
	public boolean isValid(ILaunchConfiguration configuration) {
		try {
			// Catch no TASK NAME defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.TASK_NAME)) {
				return false;
			}
			// Catch no input file defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE)) {
				setErrorMessage(Messages.MainLaunchConfigurationTab_noInpuntError);
				return false;
			} else if (!UML2ModelHelper.hasProfileApplied(
					configuration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY),
					DiceProfileConstants.SPARK_PROFILE_NAME)) {
				setErrorMessage(Messages.MainLaunchConfigurationTab_noSparkProfileApplied);
				return false;
			}
			// Catch no keep intermediate files flag defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES)) {
				return false;
			}
			// Catch no analysis type defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.SPARK_ANALYSIS_TYPE)) {
				return false;
			}
			// catch no deadline defined
			if (!configuration.hasAttribute(VerificationLaunchConfigurationAttributes.SPARK_DEADLINE)) {
				return false;
			}
			// Check input file exists
			File inputFile = new File(URI.create(configuration
					.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY)));
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
				File intermediateFilesDir = new File(URI.create(configuration.getAttribute(
						VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR, StringUtils.EMPTY)));
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
			resource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(file.getAbsolutePath()),
					true);
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

	@Override
	public Shell getTabShell() {
		return getShell();
	}

	@Override
	public void setTabDirty(boolean dirty) {
		setDirty(dirty);
		updateLaunchConfigurationDialog();
	}

}
