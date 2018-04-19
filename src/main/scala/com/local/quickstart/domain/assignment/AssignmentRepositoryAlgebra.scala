package com.local.quickstart.domain.assignment

import com.local.quickstart.domain.GenericRepositoryAlgebra

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 19/04/2018
  */
trait AssignmentRepositoryAlgebra[F[_]] extends GenericRepositoryAlgebra[F, Assignment]{
}
