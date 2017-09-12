'''
Created on Dec 28, 2016

@author: francesco
'''
import networkx as nx


class SparkDAG(object):
    '''
    Direct Acyclic Graph representing the execution plan of a Spark Job

    Attributes:
    g: graph object consisting in a networkx DiGraph instance
    json_context: original JSON data structure from which the dag is built
    labels: set of the labels which are sufficient to represent the DAG
    carry_on_labels: set of labels that each node in the graph can use to label
    its own successors

    '''

    def generateNewLabel(self):
        if self.labels:
            new_label = max(self.labels) + 1
        else:
            new_label = 0
        self.labels.add(new_label)
        print "New Label", new_label
        return new_label

    def __init__(self, json_context):
        '''
        Constructor
        '''
        self.json_context = json_context
        self.g = nx.DiGraph()
        self.labels = set()
        self.carry_on_labels = {}
        self.is_labeled = False
        # add nodes to graph
        for k, v in self.json_context["stages"].iteritems():
            self.g.add_node(k)
            if v["skipped"]:
                print "skipped"
                self.g.node[k]["skipped"] = True
                self.g.node[k]['fillcolor'] = 'red'
                self.json_context["stages"][k]["duration"] = 10.0
                self.json_context["stages"][k]["numtask"] = 1
            else:
                self.g.node[k]["skipped"] = False
                self.g.node[k]['fillcolor'] = 'white'
            # remove possible duplicates in parentIds
            self.json_context["stages"][k]["parentsIds"] =\
                list(set(v["parentsIds"]))
            # add predecessors
            for p in self.json_context["stages"][k]["parentsIds"]:
                self.g.add_edge(str(p), str(k))
            # initialize carry_on_labels to empty set
            self.carry_on_labels[k] = set()
        # identify set of starting nodes (those without predecessors)
        self.starting_nodes = [x for x in self.g.nodes()
                               if not self.g.predecessors(x)]
        for n in self.starting_nodes:
            label = self.generateNewLabel()
            self.g.node[n]["label"] = label
            self.carry_on_labels[n].add(label)

    def label_graph(self):
        '''
        visits the DAG following the precedences among stages
        and labels each node in such a way that labels can be re-used
        for stages whose execution is mutually exclusive
        '''
        visited, queue = set(), self.starting_nodes
        while queue:
            vertex = queue.pop(0)
            if vertex not in visited:
                print "Visiting {}".format(vertex)
                visited.add(vertex)
                succ = self.g.successors(vertex)
                finished_labels = False
                while succ:
                    s = succ.pop()
                    predec_s = set(self.g.predecessors(s))
                    # if all the predecessors have already been visited
                    if not (predec_s - visited):
                        queue.append(s)
                        print "labeling {}".format(s)
                        if self.carry_on_labels[vertex]:
                            l = self.carry_on_labels[vertex].pop()
                        else:
                            labelling_nodes = [x for x in predec_s
                                               if self.carry_on_labels[x]]
                            if labelling_nodes:
                                l = (self.carry_on_labels[labelling_nodes[0]]
                                     .pop())
                            else:
                                l = self.generateNewLabel()
                                finished_labels = True
                        # if s still has to be labeled, then label it
                        if "label" not in self.g.node[s]:
                            print "({}) -> {}".format(s, l)
                            self.g.node[s]["label"] = l
                            self.carry_on_labels[s].add(l)
                        if not finished_labels:
                            self.carry_on_labels[s].add(l)
                            # if there are still labels to be assigned,re-apply
                            # the procedure on all successors of vertex
                            if not succ:
                                succ = self.g.successors(vertex)
#                queue.extend(set(self.g[vertex]) - visited)
#                for n in self.g.successors(vertex):
#                    # if all the predecessors have been already visited
#                    if not (set(self.g.predecessors(n)) - visited):
#                        queue.append(n)
        # copy labels into the JSON context (should be improved)
        self.json_context["labels"] = list(self.labels)
        for i in self.json_context["stages"]:
            self.json_context["stages"][i]["label"] = self.g.node[i]["label"]
        self.is_labeled = True
        return visited
