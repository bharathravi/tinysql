import java.lang.*;
import java.util.*;
import java.sql.*;

public class tinySQL_teste
{
	private Connection dbCon;

	public tinySQL_teste( )
	{
		dbCon = null;

		try
		{
			efetuarPesquisa( );
			fecharDatabase( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		finally
		{
		}
	}

	private void efetuarPesquisa( )
		throws	SQLException,
				ClassNotFoundException
	{
		Statement 		stmt	= null;
		PreparedStatement pStmt	= null;
		ResultSet 		rs		= null;

		Class.forName( "ORG.as220.tinySQL.dbfFileDriver" );
		String stUrl = "jdbc:dbfFile:.";

		dbCon = DriverManager.getConnection( stUrl, "", "" );

		if ( dbCon != null )
		{
			stmt = dbCon.createStatement( );
      stmt.executeUpdate ("drop table if exists USUARIO");
			stmt.executeUpdate ("create table USUARIO (number numeric(4), user CHAR(20), col3 numeric(4), col4 numeric(4))" );
			mostrarRegistros( stmt.executeQuery( "select * from USUARIO" ));

			pStmt = dbCon.prepareStatement( "select * from USUARIO" );
			mostrarRegistros( pStmt.executeQuery( ));
		}
	}

	private void mostrarRegistros( ResultSet rs_ )
		throws SQLException
	{
		StringBuffer sbLinha = null;

		if ( rs_ != null )
		{
			sbLinha = new StringBuffer( );

			while ( rs_.next( ))
			{
				sbLinha.append( rs_.getString( 1 ) + " - " );
				sbLinha.append( rs_.getString( 2 ) + " - " );
				sbLinha.append( rs_.getString( 3 ) + " - " );
				sbLinha.append( rs_.getString( 4 ) + "\n" );
			}
			
			System.out.println( sbLinha.toString( ));
		}
	}

	private void fecharDatabase( )
		throws SQLException
	{
		if ( dbCon != null )
		{
			dbCon.close( );
			dbCon = null;
		}
	}

	public static void main( String args_[ ])
	{
		tinySQL_teste app = new tinySQL_teste( );

		System.exit( 0 );
	}
}