package ru.sfedu.geo.service.impl

import com.graphhopper.jsprit.core.algorithm.box.Jsprit.createAlgorithm
import com.graphhopper.jsprit.core.problem.Location
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.Builder
import com.graphhopper.jsprit.core.problem.job.Service
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupService
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl
import com.graphhopper.jsprit.core.reporting.SolutionPrinter
import com.graphhopper.jsprit.core.util.FastVehicleRoutingTransportCostsMatrix
import com.graphhopper.jsprit.core.util.Solutions
import org.springframework.stereotype.Component
import ru.sfedu.geo.model.Order
import ru.sfedu.geo.model.Point
import ru.sfedu.geo.service.PathFinder
import ru.sfedu.geo.service.VrpSolver
import ru.sfedu.geo.util.lazyLogger
import java.io.PrintWriter
import java.io.StringWriter

@Component
class CvrpSolverImpl(
    private val pathFinder: PathFinder
) : VrpSolver {

    private val log by lazyLogger()

    override fun solve(home: Point, orders: List<Order>): List<Order>? {
        val problem = Builder.newInstance().apply {
            val vehicleType = VehicleTypeImpl.Builder.newInstance("GENERIC")
                .setMaxVelocity(MAX_VELOCITY)
                .build()
            val vehicle = VehicleImpl.Builder.newInstance("Type A")
                .setStartLocation(Location.newInstance(0))
                .setType(vehicleType)
                .build()

            val jobs = orders.mapIndexed { i, order ->
                Service.Builder.newInstance(order.id.toString() + order.address)
                    .setLocation(Location.newInstance(i.inc()))
                    .setUserData(order)
                    .setServiceTime(0.1)
                    .build()
            }

            val matrix = buildMatrix(listOf(home) + orders.map { it.point })

            val costMatrix = FastVehicleRoutingTransportCostsMatrix.Builder.newInstance(matrix.size, true).apply {
                for (i in matrix.indices) {
                    for (j in matrix.indices) {
                        if (i != j) {
                            addTransportDistance(i, j, matrix[i][j])
                        }
                    }
                }
            }.build()

            addAllJobs(jobs)
            setRoutingCost(costMatrix)
            addAllVehicles(listOf(vehicle))
            setFleetSize(VehicleRoutingProblem.FleetSize.FINITE)
        }.build()

        val algorithm = createAlgorithm(problem).apply { maxIterations = 256 }

        val solution = Solutions.bestOf(algorithm.searchSolutions()).also {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            SolutionPrinter.print(printWriter, problem, it, SolutionPrinter.Print.VERBOSE)
            log.info(stringWriter.toString())
        }

        return solution?.run {
            routes.firstOrNull()?.activities?.map {
                (it as PickupService).job.userData as Order
            }
        }
    }

    private fun buildMatrix(points: List<Point?>): Array<DoubleArray> {
        log.debug("create matrix: size={}", points.size)
        val size = points.size
        val matrix = Array(size) { DoubleArray(size) }
        for (i in points.indices) {
            log.debug("create matrix row={} of {}", i, size)
            val ip = points[i]
            for (j in points.indices) {
                val jp = points[j]
                if (ip !== jp) {
                    matrix[i][j] = when {
                        ip == null || jp == null -> Double.MAX_VALUE
                        else -> pathFinder.distance(ip, jp)
                    }
                } else {
                    matrix[i][j] = 0.0
                }
            }
        }
        return matrix
    }


    companion object {
        // СИ 7 м/с = 25 км/ч средняя скорость передвижения по городу
        const val MAX_VELOCITY = 7.0
    }

}


