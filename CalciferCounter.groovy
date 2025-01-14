import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.svg.SVGLoad

//println "Clearing the Vitamins cache to make sure current geometry is being used (only run this operation when the STL has changed)"
//Vitamins.clear()

CSG calcifer, counter, cutter, outline

def name = calcifer

def repoName = "https://github.com/JansenSmith/CalciferCounter.git"
def fileLoc = "calcifer/calcifer_mini_Front_30x40.stl"
println "Loading piece STL from repo located here: "+fileLoc
calciferSTL = ScriptingEngine.fileFromGit(
	repoName,
	fileLoc);

println "Converting piece STL to CSG"
// Load the .CSG from the disk and cache it in memory
calcifer  = Vitamins.get(calciferSTL);
println "The original calcifer STL is "+calcifer.totalZ+"mm in Z thickness"

fileLoc = "counter/front-cover.stl"
println "Loading counter STL from repo located here: "+fileLoc
counterSTL = ScriptingEngine.fileFromGit(
	repoName,
	fileLoc);

println "Converting counter STL to CSG"
// Load the .CSG from the disk and cache it in memory
counter  = Vitamins.get(counterSTL);


fileLoc = "calcifer/calcifer_mini_outline.svg"
def depth = calcifer.totalZ

println "Importing outline SVG"
File f = ScriptingEngine
	.fileFromGit(
		repoName,//git repo URL
		"main",//branch
		"calcifer/calcifer_mini_outline.svg"// File from within the Git repo
	)
println "Extruding SVG "+f.getAbsolutePath()
SVGLoad s = new SVGLoad(f.toURI())
println "Layers= "+s.getLayers()
outline = s.extrudeLayerToCSG(depth,"outside")

println "Moving calcifer into position"
calcifer = calcifer.roty(180).toZMin()
				.movex(-8)
				.movey(6)

println "Moving outline into position"
outline = outline.roty(180).toZMin()
				.toXMin().movex(calcifer.minX)
				.toYMin().movey(calcifer.minY)
				.movex(3)
				.movey(3.75)

//return outline
				
println "Making outline slightly larger"
outline = outline.hull()
def outline_scale = 1.05
def outline_posX = outline.centerX
def outline_posY = outline.centerY
outline = outline.movex(-outline_posX)
				.movey(-outline_posY)
				.scalex(outline_scale)
				.scaley(outline_scale)
				.movex(outline_posX)
				.movey(outline_posY)

println "Creating cutter from outline"
cutter = calcifer.boundingBox
cutter = cutter.difference(outline)

println "Trimming calcifer to outline"
calcifer = calcifer.difference(cutter)

//counter = counter.difference(cutter)

println "Setting CSG attributes"
if (calcifer) {
	calcifer = calcifer.setColor(javafx.scene.paint.Color.ORANGE)
				.setName(name+"_painting")
				.addAssemblyStep(0, new Transform())
				.setIsWireFrame(false)
				.setManufacturing({ toMfg ->
					return toMfg
							//.rotx(180)// fix the orientation
							//.toZMin()//move it down to the flat surface
				})
}

if (counter) {
	counter = counter.setColor(javafx.scene.paint.Color.LIGHTBLUE)
				.setName(name+"_counter")
				.addAssemblyStep(0, new Transform())
				.setIsWireFrame(false)
				.setManufacturing({ toMfg ->
					return toMfg
							//.rotx(180)// fix the orientation
							//.toZMin()//move it down to the flat surface
				})
}

if (cutter) {
	cutter = cutter.setColor(javafx.scene.paint.Color.DARKGRAY)
				.setName(name+"_cutter")
				.addAssemblyStep(0, new Transform())
				.setManufacturing({ toMfg ->
					return toMfg
							//.rotx(180)// fix the orientation
							//.toZMin()//move it down to the flat surface
				})
}

if (outline) {
	outline = outline.setColor(javafx.scene.paint.Color.BLACK)
				.setName(name+"_outline")
				.addAssemblyStep(0, new Transform())
				.setIsWireFrame(true)
				.setManufacturing({ toMfg ->
					return toMfg
							//.rotx(180)// fix the orientation
							//.toZMin()//move it down to the flat surface
				})
}

//ret = piece.difference(cutter)
//ret = counter.intersect(cutter)
//ret = [calcifer, counter]
//ret = [calcifer, cutter, outline]
ret = calcifer

return ret