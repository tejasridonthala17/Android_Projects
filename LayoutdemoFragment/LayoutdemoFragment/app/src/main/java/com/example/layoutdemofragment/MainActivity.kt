package com.example.layoutdemofragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var spinner: Spinner
    private lateinit var cbTable: CheckBox
    private lateinit var cbGrid: CheckBox
    private lateinit var cbCard: CheckBox
    private lateinit var cbRecycler: CheckBox
    private lateinit var radioGroup: RadioGroup
    private lateinit var rbTable: RadioButton
    private lateinit var rbGrid: RadioButton
    private lateinit var rbCard: RadioButton
    private lateinit var rbRecycler: RadioButton
    private lateinit var fragmentWrapper: FrameLayout
    private lateinit var btnRollBack: Button

    private var currentMode: String = ""
    private var previousMode: String = "Grid View"
    private var isSyncing: Boolean = false

    private val options = arrayOf("Table View", "Grid View", "Card View", "Recycler View")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        spinner = findViewById(R.id.viewSpinner)
        cbTable = findViewById(R.id.cbTableView)
        cbGrid = findViewById(R.id.cbGridView)
        cbCard = findViewById(R.id.cbCardView)
        cbRecycler = findViewById(R.id.cbRecyclerView)
        radioGroup = findViewById(R.id.radioGroup)
        rbTable = findViewById(R.id.rbTable)
        rbGrid = findViewById(R.id.rbGrid)
        rbCard = findViewById(R.id.rbCard)
        rbRecycler = findViewById(R.id.rbRecycler)
        fragmentWrapper = findViewById(R.id.fragment_wrapper)
        btnRollBack = findViewById(R.id.btnRollBack)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isSyncing = true
        setupTabLayout()
        setupSpinner()
        setupCheckboxes()
        setupRadioButtons()
        isSyncing = false

        btnRollBack.setOnClickListener {
            if (previousMode.isNotEmpty()) {
                syncAll(previousMode)
            }
        }

        // Initial Load
        if (savedInstanceState == null) {
            syncAll("Grid View")
        }
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (isSyncing) return
                val pos = tab?.position ?: return
                val selected = options.getOrNull(pos) ?: return
                syncAll(selected)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isSyncing) return
                val selected = options.getOrNull(position) ?: return
                syncAll(selected)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupCheckboxes() {
        val listener = View.OnClickListener { v ->
            if (isSyncing) return@OnClickListener
            val cb = v as CheckBox
            if (cb.isChecked) {
                val mode = when (cb.id) {
                    R.id.cbTableView -> "Table View"
                    R.id.cbGridView -> "Grid View"
                    R.id.cbCardView -> "Card View"
                    R.id.cbRecyclerView -> "Recycler View"
                    else -> "Grid View"
                }
                syncAll(mode)
            } else {
                cb.isChecked = true
            }
        }
        cbTable.setOnClickListener(listener)
        cbGrid.setOnClickListener(listener)
        cbCard.setOnClickListener(listener)
        cbRecycler.setOnClickListener(listener)
    }

    private fun setupRadioButtons() {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isSyncing || checkedId == -1) return@setOnCheckedChangeListener
            val mode = when (checkedId) {
                R.id.rbTable -> "Table View"
                R.id.rbGrid -> "Grid View"
                R.id.rbCard -> "Card View"
                R.id.rbRecycler -> "Recycler View"
                else -> return@setOnCheckedChangeListener
            }
            syncAll(mode)
        }
    }

    private fun syncAll(selected: String) {
        if (isSyncing) return
        if (selected == currentMode && currentMode.isNotEmpty()) return

        isSyncing = true
        try {
            if (currentMode.isNotEmpty()) {
                previousMode = currentMode
            } else {
                previousMode = selected
            }
            currentMode = selected

            // 1. Sync TabLayout
            val tabIndex = options.indexOf(selected)
            if (tabIndex != -1 && tabLayout.selectedTabPosition != tabIndex) {
                tabLayout.getTabAt(tabIndex)?.select()
            }

            // 2. Sync Spinner
            val spinnerAdapter = spinner.adapter as? ArrayAdapter<String>
            val spinnerIndex = spinnerAdapter?.getPosition(selected) ?: -1
            if (spinnerIndex != -1 && spinner.selectedItemPosition != spinnerIndex) {
                spinner.setSelection(spinnerIndex)
            }

            // 3. Sync Checkboxes
            cbTable.isChecked = (selected == "Table View")
            cbGrid.isChecked = (selected == "Grid View")
            cbCard.isChecked = (selected == "Card View")
            cbRecycler.isChecked = (selected == "Recycler View")

            // 4. Sync RadioButtons
            val rbToSelect = when (selected) {
                "Table View" -> rbTable
                "Grid View" -> rbGrid
                "Card View" -> rbCard
                "Recycler View" -> rbRecycler
                else -> null
            }
            if (rbToSelect != null && !rbToSelect.isChecked) {
                rbToSelect.isChecked = true
            }

            // 5. Update Border Color
            val color = when (selected) {
                "Table View" -> "#FF0000" // Red
                "Grid View" -> "#00FF00"  // Green
                "Card View" -> "#BB86FC"  // Purple
                "Recycler View" -> "#03DAC5" // Teal
                else -> "#FFFFFF"
            }

            val background = fragmentWrapper.background?.mutate() as? GradientDrawable
            background?.setStroke(8, Color.parseColor(color))

            // 6. Update Fragment
            updateFragment(selected)
        } finally {
            isSyncing = false
        }
    }

    private fun updateFragment(mode: String) {
        val fragment: Fragment = when (mode) {
            "Table View" -> TableFragment()
            "Grid View" -> GridFragment()
            "Card View" -> CardFragment()
            "Recycler View" -> RecyclerFragment()
            else -> GridFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
