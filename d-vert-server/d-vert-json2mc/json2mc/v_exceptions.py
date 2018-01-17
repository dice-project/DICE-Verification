
class VerificationException(Exception):
    """
    Base class for all exception errors
    """
    def __init__(self, msg):
        self.msg = msg

    def __str__(self):
        return self.msg