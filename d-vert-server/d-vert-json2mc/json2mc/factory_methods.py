'''
Created on Feb 10, 2017

@author: Francesco Marconi
'''
import config as cfg

class DiaVerificationFactory(object):
    '''
    classdocs
    '''
    __supported_techs = cfg.SUPPORTED_TECHS


    @staticmethod
    def get_verif_task(tech, template_path=None, context=None, output_dir=None, display=False,
                       plotonly_folder=None, graphical_conf_path=None):
        v_task = DiaVerificationFactory.__supported_techs.get(tech.lower(), None)
        if v_task:
            if not plotonly_folder:
                return v_task(template_path=template_path,
                              context=context,
                              output_dir=output_dir,
                              display=display)
            else:
                return v_task(plotonly_folder=plotonly_folder,
                              display=display,
                              graphical_conf_path=graphical_conf_path)
        else:
            raise NotImplementedError("The requested technology ({}) is not supported.")