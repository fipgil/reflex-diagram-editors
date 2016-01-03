to be able to edit/build/run this plugin you need a recent Eclipse Modelling
Tools installation & install all Graphiti features.
They are available in the following p2 repository
 Mars - http://download.eclipse.org/releases/mars

(tested configuration)
    Eclipse Modeling Tools	4.5.1.20150917-1200
    Graphiti (Incubation)	0.12.1.v20150916-0905
    Graphiti Examples (Incubation)	0.12.1.v20150916-0905
    Graphiti Export (Incubation)	0.12.1.v20150916-0905
    Graphiti SDK (Incubation)	0.12.1.v20150916-0905
    Graphiti SDK Plus (Incubation)	0.12.1.v20150916-0905
    Graphiti Tools (Incubation)	0.12.1.v20150916-0905

to test editor:
- open Plug-in development perspective (see Window menu)
- reset the perspective if the Package Explorer view is not visible
- in the Package Explorer view select the fr.gil.artics.tutorial.graphiti.fbd
- run Run As/Eclipse Application contextual menu or Debug As/Eclipse Application

diagram outputs and block outputs are not yet implemented

to start with the editor:
- create a general project
- create a new file with the fbd extension
- add inputs to this file
- save it.
- create a new fbd file
- add a block (use *.fbd as a filter)