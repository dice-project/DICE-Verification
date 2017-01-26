'''
Created on Dec 20, 2016

@author: francesco
'''
import graphviz


class DAGRenderer(object):
    '''
    Utility class for rendering graphs starting from networkx digraph
    Based on graphviz python library (https://pypi.python.org/pypi/graphviz)
    '''

    def __init__(self, graph):
        '''
        Constructor

        self.dot = Digraph(comment='My plot')
        self.json_context = json_context
        for k in json_context.keys():
            self.dot.node(k, "STAGE {}".format(k))
            for p in set(json_context[k]["parentsIds"]):
                self.dot.edge(str(p), str(k))
        '''
        self.dot = graphviz.Digraph(comment='My plot')
        self.g = graph
        for n in sorted(graph.nodes()):
            print n
            if "label" in graph.node[n]:
                self.dot.node(n, "S{}\n<{}>".format(n, graph.node[n]["label"]),
                              style="filled",
                              fillcolor=graph.node[n]["fillcolor"])
            else:
                self.dot.node(n, "S{}".format(n))
            for p in graph.predecessors(n):
                self.dot.edge(p, n)
        print self.dot.source

    def render(self, path=None, view=False):
        self.dot.render(path, view=view)
